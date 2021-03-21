package yt.richard.marcidus.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import yt.richard.marcidus.readBytesWithCustomAgent
import java.net.URL

object RedditUtils {

    fun getPosts(subreddit: String?, category: String = "hot", limit: Int = 20): List<RedditPost> {
        return try {
            val content = URL("https://www.reddit.com/r/$subreddit/$category.json?limit=$limit&raw_json=1").readBytesWithCustomAgent().toString(Charsets.UTF_8) // get raw json data
            val postsJson = JsonParser.parseString(content).asJsonObject["data"].asJsonObject["children"].asJsonArray // parse post json array
            postsJson.map { Gson().fromJson(it.asJsonObject["data"], RedditPost::class.java) } // post json array to deserialized RedditPost object list
        } catch (e: Exception) {
            println("Error occurred whilst trying to get posts from subreddit $subreddit: ${e.message}")
            emptyList()
        }
    }

    // currently unused, was previously used for debugging purposes (test specific post mirroring)
    fun getPost(url: String): RedditPost? {
        return try {
            val content = URL("$url.json?raw_json=1").readBytesWithCustomAgent().toString(Charsets.UTF_8) // get raw json data
            val postJson = JsonParser.parseString(content).asJsonArray[0].asJsonObject["data"].asJsonObject["children"].asJsonArray[0].asJsonObject["data"] // parse post json
            Gson().fromJson(postJson, RedditPost::class.java) // deserialize to RedditPost object
        } catch (e: Exception) {
            println("Error occurred whilst trying to get post from $url")
            null
        }
    }

    class RedditPost {
        // reddit data struct (incomplete)
        val title: String? = null
        val author: String? = null
        val id: String? = null
        val permalink: String? = null
        val is_self: Boolean? = null
        val url: String? = null
        val is_video: Boolean? = null
        val is_gallery: Boolean? = null
        val gallery_data: JsonObject? = null
        val media_metadata: JsonObject? = null
        val over_18: Boolean? = null
        val score: Int? = null

        // extension functions
        override fun toString(): String {
            return "\"$title\" by /u/$author (https://redd.it/$id)"
        }
    }
}
