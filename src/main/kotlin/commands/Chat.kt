package org.ritsu.mirai.plugin.commands

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

fun chat(msg: String): String {
    val url = "https://api.iyk0.com/liaotian/?msg=$msg"
    val client = OkHttpClient().also {
        it.newBuilder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            proxy(Proxy.NO_PROXY)
        }
    }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val inputStream = response.body?.byteStream() ?: return "无法获取body!"
        return inputStream.bufferedReader(Charset.forName("UTF-8")).readText().replace("菲菲", "kgg")
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时!"
    } catch (e: Exception) {
        return "未知错误!"
    }
}