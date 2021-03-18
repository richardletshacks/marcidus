package yt.richard.marcidus

import yt.richard.marcidus.utils.IGLoginUtils
import kotlin.system.exitProcess

suspend fun main() {
    checkDependencies()
    val client = IGLoginUtils.login() // login
    PostScheduler(client).start() // start new postscheduler instance
}

fun checkDependencies() {
    try {
        Runtime.getRuntime().exec("youtube-dl")
    } catch (e: Exception) {
        println("youtube-dl not found. Exiting.") // TODO: disable video reposting instead of exiting
        exitProcess(0)
    }
}
