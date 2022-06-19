package org.ritsu.mirai.plugin.commands

/**
 * @author wcy
 * @return 返回信息, 图片路径
 */
fun getCalendar(): Pair<String, String?> {
    val url = "https://api.vvhan.com/api/moyu"
    return try {
        val path = "./data/Image/temp_calendar.jpg"
        val (code, msg) = download(url, path)
        if (code == 200 && msg == null) Pair("Success!", path)
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}