package yt.richard.marcidus

import com.google.gson.*
import java.io.File
import kotlin.system.exitProcess

object ConfigManager {

    private val config = parseConfig()
    fun getConfig() = config

    private fun parseConfig(): Config {
        return try {
            val config = Gson().fromJson(File("config.json").readText(), Config::class.java) // deserialize config.json to Config object
            if(config.javaClass.declaredFields.any { it.isAccessible = true; it.get(config) == null }) throw JsonParseException("One or more keys missing") // throw error if some key is missing
            config // return config
        } catch (e: Exception) {
            println("Failed to parse config.json (${e.message}). Generating new config file.")
            createDefaultConfig()
            exitProcess(0) // exit after generating new config
        }
    }

    private fun createDefaultConfig() {
        val json = GsonBuilder().setPrettyPrinting().create().toJson(hashMapOf(
            "username" to "example",
            "password" to "dummy123",
            "subreddits" to listOf("memes", "dankmemes"),
            "min_delay" to 3600000,
            "max_delay" to 5400000,
            "description" to "{title}\n\nby /u/{author} in /r/{subreddit} (https://redd.it/{id})"
        ))
        File("config.json").writeText(json)
    }

    class Config {
        val username: String? = null
        val password: String? = null
        val subreddits: List<String>? = null
        val min_delay: Long? = null
        val max_delay: Long? = null
        val description: String? = null
    }
}
