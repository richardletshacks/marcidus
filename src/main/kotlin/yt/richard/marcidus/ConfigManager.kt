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
        javaClass.classLoader.getResourceAsStream("config.json").use { File("config.json").writeBytes(it?.readBytes() ?: return) }
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
