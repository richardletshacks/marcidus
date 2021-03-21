package yt.richard.marcidus

import yt.richard.marcidus.utils.IGLoginUtils
import java.net.URL
import kotlin.system.exitProcess

suspend fun main() {
    checkDependencies()
    val client = IGLoginUtils.login() // login
    PostScheduler(client).start() // start new postscheduler instance
}

private fun checkDependencies() {
    try {
        Runtime.getRuntime().exec("youtube-dl")
    } catch (e: Exception) {
        println("youtube-dl not found. Exiting.") // TODO: disable video reposting instead of exiting
        exitProcess(0)
    }
}

/// EXTENSION FUNCTIONS

// top level extension function for reading content with custom user agent (bypass jvm block on some sites)
fun URL.readBytesWithCustomAgent(): ByteArray {
    val connection = openConnection() // open connection
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36") // set user-agent
    return connection.getInputStream().use { it.readBytes() } // return content and close stream
}
