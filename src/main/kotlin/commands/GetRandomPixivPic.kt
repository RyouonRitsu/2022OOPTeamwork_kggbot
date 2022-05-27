package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request

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
            val (code, msg) = downloadPicture(original, "./data/Image/temp_pixiv.jpg")
            if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_pixiv.jpg")
            else Pair("Error: $code, $msg", original)
        }
        else -> Pair("Error: ${response.code}\n", null)
    }
}