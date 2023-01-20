package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @return 返回信息, 图片路径
 */
fun getNews(): Pair<String, String?> {
    val url = "https://api.iyk0.com/60s"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
        return when (response.code) {
            200 -> {
                val jsonObject = JSON.parseObject(body)
                if (jsonObject.getString("code") == "200") {
                    val path = "./data/Image/temp_news.jpg"
                    val (code, msg) = download(jsonObject.getString("imageUrl"), path)
                    if (code == 200 && msg == null) Pair("Success!", path)
                    else Pair("Error: $code, $msg", null)
                } else Pair("Error: ${jsonObject.getString("code")}\n", null)
            }
            else -> Pair("Error: ${response.code}\n", null)
        }
    } catch (e: java.net.SocketTimeoutException) {
        return Pair("连接超时！\n", null)
    } catch (e: Exception) {
        return Pair("未知错误！\n", null)
    }
}