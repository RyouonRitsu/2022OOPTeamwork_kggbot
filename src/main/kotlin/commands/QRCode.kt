package org.ritsu.mirai.plugin.commands

fun qrCode(content: String, name: String? = "temp_QRCode"): Pair<String, String?> {
    if (content == "") return Pair("二维码的内容是？\n", null)
    val url = "https://api.vvhan.com/api/qr?text=$content"
    val path = "./data/Image/$name.jpg"
    return try {
        val (code, msg) = download(url, path)
        if (code == 200 && msg == null) Pair("Success!", path)
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}