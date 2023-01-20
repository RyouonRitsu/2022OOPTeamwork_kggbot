package org.ritsu.mirai.plugin.commands

/**
 * @author wcy
 * @param content 二维码的内容
 * @param name 二维码图片的文件名称
 * @return 返回信息
 */
fun qrCode(content: String, name: String? = "temp_QRCode"): Pair<String, String?> {
    if (content == "") return Pair("二维码的内容是？\n", null)
    val text = content.replace("&", " ")
    val url = "https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=$text"
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