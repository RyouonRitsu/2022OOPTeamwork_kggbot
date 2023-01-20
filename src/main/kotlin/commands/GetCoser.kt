package org.ritsu.mirai.plugin.commands

import org.ritsu.mirai.plugin.entity.loadImage
import org.ritsu.mirai.plugin.entity.save
import java.io.File

/**
 * @author wcy
 * @return 返回信息, 图片路径
 */
fun getCoser(): Pair<String, String?> {
    val url = "https://api.iyk0.com/cos"
    return try {
        val path = "./data/Image/temp_coser.jpg"
        val (code, msg) = download(url, path)
        loadImage(File(path)).save(File(path))
        if (code == 200 && msg == null) Pair("Success!", path)
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}