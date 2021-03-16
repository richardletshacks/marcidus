package yt.richard.marcidus

import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.actions.timeline.TimelineAction
import kotlinx.coroutines.*
import yt.richard.marcidus.utils.ImageUtils
import yt.richard.marcidus.utils.RedditUtils
import yt.richard.marcidus.utils.RedditUtils.RedditPost
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class PostScheduler(private val client: IGClient) {

    // load values from config
    private val subreddits = ConfigManager.getConfig().subreddits!!
    private val minDelay = ConfigManager.getConfig().min_delay!!
    private val maxDelay = ConfigManager.getConfig().max_delay!!

    // init cache (no double repost)
    private val cache = mutableListOf<String>()

    suspend fun start() = coroutineScope {
        // launch post coroutine
        launch {
            while(isActive) {
                val posts = RedditUtils.getPosts(subreddits.random()).filter { cache.contains(it.url).not() } // retrieve a list of posts
                val randomPost = if(posts.isEmpty().not()) posts.random() else continue // get random post
                cache.push(randomPost.url ?: continue, 30) // push post to cache so it isn't posted twice
                if(post(randomPost).not()) continue // if failed to post, try again with new post
                val delay = (minDelay..maxDelay).random()
                println("Posting next post in ${SimpleDateFormat("HH:mm:ss").format(Date(delay - TimeZone.getDefault().rawOffset))}")
                delay(delay) // wait random delay
            }
        }
    }

    private fun post(post: RedditPost): Boolean {
        return if(post.over_18 == true || post.is_self == true)
            false // we don't want nsfw and non media posts
        else if(post.is_video == true)
            postVideo(post)
        else if(post.is_gallery == true)
            postGallery(post)
        else
            postImage(post)
    }

    private fun postVideo(post: RedditPost): Boolean {
        Runtime.getRuntime().exec("youtube-dl --o temp --write-thumbnail https://reddit.com${post.permalink}").waitFor() // await youtube-dl download
        val video = File("temp.mp4").readBytes(); File("temp.mp4").delete() // read video into memory then delete file                              TODO: pipe youtube-dl output so no need to save files
        val thumbnail = File("temp.png").readBytes(); File("temp.png").delete() // read thumbnail into memory and delete file
        return client.actions.timeline().uploadVideo(video, thumbnail, post.createDescription()).handle { success, error ->
            return@handle if(success != null) {
                println("Successfully posted video $post")
                true
            } else {
                println("Failed to repost video $post: ${error.message}")
                false
            }
        }.join()
    }

    private fun postGallery(post: RedditPost): Boolean {
        val galleryItems = post.gallery_data?.get("items")?.asJsonArray?.map { it.asJsonObject["media_id"].asString } ?: return false // retrieve list of gallery item ids
        if(galleryItems.size > 10) return false // if more than 10 items fail (instagram limit)
        val mediaItems = galleryItems.map { item -> post.media_metadata?.let { it[item].asJsonObject["s"].asJsonObject["u"].asString } ?: return false } // list of direct media links using previously retrieved item ids (data structure is id.s.u)
        val sidecarItems = mediaItems.map { TimelineAction.SidecarPhoto(URL(it).readBytes()) } // load media into instagram album objects
        return client.actions.timeline().uploadAlbum(sidecarItems, post.createDescription()).handle { success, error ->
            return@handle if(success != null) {
                println("Successfully posted album $post")
                true
            } else {
                println("Failed to repost album $post: ${error.message}")
                false
            }
        }.join()
    }

    private fun postImage(post: RedditPost, makeSquare: Boolean = false): Boolean {
        val img = URL(post.url).readBytes().let { if(makeSquare) ImageUtils.makeSquare(it) else it } // load image and make it squared if previously failed due to non instagram aspect ratio
        return client.actions.timeline().uploadPhoto(img, post.createDescription()).handle { success, error ->
            return@handle if(success != null) {
                println("Successfully posted image $post")
                true
            } else {
                if(error.message?.contains("aspect ratio") == true)
                    return@handle postImage(post, true) // if failed to post due to non instagram aspect ratio try again but make it squared
                else
                    println("Failed to repost image $post: ${error.message}")
                false
            }
        }.join()
    }

    private fun RedditPost.createDescription(): String {
        val template = ConfigManager.getConfig().description!! // load description template from config
        val placeholders = javaClass.declaredFields.associateBy({ it.isAccessible = true; it.name }, { it.get(this)?.toString().orEmpty() }) // load placeholder map from post object using reflection
        return placeholders.entries.fold(template) { it, (key, value) -> it.replace("{$key}", value) } // finally replace placeholders in template string and return result
    }

    private fun MutableList<String>.push(element: String, maxSize: Int) {
        if(size > maxSize) removeAt(0) // remove element at index 0 if more elements than max cache size
        add(element) // add new element to cache
    }
}
