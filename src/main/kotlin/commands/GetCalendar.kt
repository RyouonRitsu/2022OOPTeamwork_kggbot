package org.ritsu.mirai.plugin.commands

fun getCalendar(): Pair<String, String?> {
    val url = "https://api.vvhan.com/api/moyu"
    return try {
        val (code, msg) = download(url, "./data/Image/temp_calendar.jpg")
        if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_calendar.jpg")
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}