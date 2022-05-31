package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ritsu.mirai.plugin.entity.Administrator
import java.net.Proxy

fun getRandomPixivPic(id: Long, info: String? = null): Pair<String, String?> {
    if (id !in Administrator.administrators) return Pair("你不是管理员, 无法使用此命令", null)
    var url = "https://api.lolicon.app/setu/v2?"
    if (info != null) {
        if (info.startsWith("r18") || info.startsWith("R18")) url += "r18=1&"
        val tag = info.replace("r18", "").replace("R18", "")
        val list = tag.split("&")
        list.forEach { s -> url += "tag=$s&" }
    }
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
                val (code, msg) = download(
                    original,
                    if (original.endsWith("jpg")) "./data/Image/temp_pixiv.jpg" else "./data/Image/temp_pixiv.png"
                )
                if (code == 200 && msg == null) Pair(
                    "Success!",
                    if (original.endsWith("jpg")) "./data/Image/temp_pixiv.jpg" else "./data/Image/temp_pixiv.png"
                )
                else Pair("Error: $code, $msg", original)
            }
            else -> Pair("Error: ${response.code}\n", null)
        }
    } catch (_: Exception) {
        return Pair("抱歉，没找到图呢~", null)
    }
}