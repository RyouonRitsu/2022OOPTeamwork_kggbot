package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @return 返回信息, api返回的内容
 */
fun lick(): Pair<String, String?> {
    val url = "https://api.iyk0.com/chp"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
        return when (response.code) {
            200 -> {
                val jsonObject = JSON.parseObject(body)
                val result = jsonObject.getString("txt")
                Pair("success", result)
            }
            else -> Pair("Error: ${response.code}\n", null)
        }
    } catch (e: java.net.SocketTimeoutException) {
        return Pair("连接超时！\n", null)
    } catch (e: Exception) {
        return Pair("未知错误！\n", null)
    }
}