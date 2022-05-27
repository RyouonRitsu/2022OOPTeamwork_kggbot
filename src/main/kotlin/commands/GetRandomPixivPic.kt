package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress

fun getRandomPixivPic(): Pair<String, String?> {
    val url = "https://api.lolicon.app/setu/v2"
    val client = OkHttpClient()
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    return when (response.code) {
        200 -> {
            val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
            val jsonObject = JSON.parseObject(body)
            val data = jsonObject.getJSONArray("data").getJSONObject(0)
            val original = data.getJSONObject("urls").getString("original")
            val sa = InetSocketAddress("127.0.0.1:64195", 7890)
            val (code, msg) = downloadPicture(original, "./data/Image/temp_pixiv.jpg", sa)
            if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_pixiv.jpg")
            else Pair("Error: $code, $msg", original)
        }
        else -> Pair("Error: ${response.code}\n", null)
    }
}