package org.ritsu.mirai.plugin.commands

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

fun getMetar(airport: String): String {
    val url = "https://aviationweather.gov/metar/data?ids=$airport&format=raw&date=&hours=0"
    val client = OkHttpClient().also {
        it.newBuilder().apply {
            proxy(Proxy.NO_PROXY)
        }
    }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        var body = response.body?.string() ?: return "无法获取body!\n"
        return when (body.indexOf("No METAR found")) {
            -1 -> {
                try {
                    if (body.indexOf(">$airport") == -1) return "No METAR found"
                    body = body.substring(body.indexOf(">$airport") + 1, body.length)
                    body.substring(0, body.indexOf("</code>"))
                } catch (e: Exception) {
                    "解析错误: ${e.message}\n"
                }
            }
            else -> "No METAR found"
        }
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时"
    } catch (e: Exception) {
        return "未知错误"
    }
}