package yt.richard.marcidus

import yt.richard.marcidus.utils.IGLoginUtils

suspend fun main() {
    val client = IGLoginUtils.login() // login
    PostScheduler(client).start() // start new postscheduler instance
}
