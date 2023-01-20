package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ritsu.mirai.plugin.entity.loadImage
import org.ritsu.mirai.plugin.entity.save
import java.io.File
import java.net.Proxy

/**
 * 随机色图
 *
 * @author RyouonRitsu, wcy
 * @param info 命令信息
 * @return Pair(错误信息, 图片地址)
 */
fun getRandomPixivPic(info: String): Pair<String, String?> {
    var url = "https://api.lolicon.app/setu/v2?"
    val tag: String
    if (info.startsWith("mix")) {
        url += "r18=2&"
        tag = info.replaceFirst("mix", "")
    } else if (info.startsWith("r18") || info.startsWith("R18")) {
        url += "r18=1&"
        tag = info.replace("r18", "").replace("R18", "")
    } else tag = info
    val list = tag.split("&")
    list.forEach { s -> url += "tag=$s&" }
    try {
        val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
        val request = Request.Builder().get().url(url).build()
        val response = client.newCall(request).execute()
        return when (response.code) {
            200 -> {
                val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
                val jsonObject = JSON.parseObject(body)
                val data = jsonObject.getJSONArray("data").getJSONObject(0)
                val original = data.getJSONObject("urls").getString("original")
                val path =
                    if (original.endsWith("jpg")) "./data/Image/temp_pixiv.jpg" else "./data/Image/temp_pixiv.png"
                val (code, msg) = download(original, path)
                loadImage(File(path)).save(File(path))
                if (code == 200 && msg == null) Pair("Success!", path)
                else Pair("Error: $code, $msg", original)
            }
            else -> Pair("Error: ${response.code}\n", null)
        }
    } catch (_: Exception) {
        return Pair("抱歉，没找到图呢~", null)
    }
}