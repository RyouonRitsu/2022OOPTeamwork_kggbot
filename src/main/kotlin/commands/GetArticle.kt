package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param str 待生成文章的内容和字数
 * @return 生成文章结果
 */
fun getArticle(str: String): String {
    if (str == "") return "你要生成什么样的文章呢？\n"
    val content: String
    val num: String
    if (str.contains("&")) {
        content = str.substring(0, str.indexOf("&"))
        num = str.substring(str.indexOf("&"), str.length).replaceFirst("&", "")
    } else {
        content = str
        num = ""
    }
    val url = "https://api.iyk0.com/gpbt/?msg=$content&num=$num"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return "无法获取body!\n"
        return when (response.code) {
            200 -> {
                val jsonObject = JSON.parseObject(body)
                if (jsonObject.getString("code") == "200") {
                    jsonObject.getString("data")
                } else "Error: ${jsonObject.getString("code")}\n"
            }
            else -> "Error: ${response.code}\n"
        }
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时！\n"
    } catch (e: Exception) {
        return "未知错误！\n"
    }
}