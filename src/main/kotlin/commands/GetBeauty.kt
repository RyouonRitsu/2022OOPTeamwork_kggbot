package org.ritsu.mirai.plugin.commands

import org.ritsu.mirai.plugin.entity.loadImage
import org.ritsu.mirai.plugin.entity.save
import java.io.File

fun getBeauty(): Pair<String, String?> {
//    val url = "http://api.weijieyue.cn/api/youhuo/api.php?return=img"
    val url = "https://api.iyk0.com/mtyh"
    return try {
        val (code, msg) = download(url, "./data/Image/temp_beauty.png")
        loadImage(File("./data/Image/temp_beauty.png")).save(File("./data/Image/temp_beauty.png"))
        if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_beauty.png")
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}