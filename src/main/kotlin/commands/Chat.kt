package org.ritsu.mirai.plugin.commands

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

fun chat(hi: String): String {
    val url = "https://api.iyk0.com/liaotian/?msg=$hi"
    val client = OkHttpClient().also {
        it.newBuilder().apply {
            proxy(Proxy.NO_PROXY)
        }
    }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val inputStream = response.body?.byteStream() ?: return "无法获取body!\n"
        return inputStream.bufferedReader().readText()
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时！\n"
    } catch (e: Exception) {
        return "未知错误！\n"
    }
}