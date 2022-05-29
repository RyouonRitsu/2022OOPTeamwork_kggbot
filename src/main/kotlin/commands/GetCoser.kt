package org.ritsu.mirai.plugin.commands

fun getCoser(): Pair<String, String?> {
    val url = "https://api.iyk0.com/cos"
    return try {
        val (code, msg) = download(url, "./data/Image/temp_coser.jpg")
        if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_coser.jpg")
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}