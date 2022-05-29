package org.ritsu.mirai.plugin.commands

fun getCat(): Pair<String, String?> {
    val url = "http://edgecats.net"
    return try {
        val (code, msg) = download(url, "./data/Image/temp_cat.gif")
        if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_cat.gif")
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}