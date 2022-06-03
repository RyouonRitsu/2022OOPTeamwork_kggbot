package org.ritsu.mirai.plugin.commands

fun like(id: Long): Pair<String, String?> {
    val url = "http://api.weijieyue.cn/api/tupian/zan.php?qq=$id"
    return try {
        val (code, msg) = download(url, "./data/Image/like$id.jpg")
        if (code == 200 && msg == null) Pair("Success!", "./data/Image/like$id.jpg")
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}