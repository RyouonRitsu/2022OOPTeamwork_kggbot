package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param info 待加密的内容
 * @return 加密结果
 */
fun encode(info: String): String {
    if (info == "") return "请告诉我你要加密的内容\n"
    if ("&" !in info) return "要加密的内容和key之间请用\"&\"连接\n"
    val content = info.substring(0, info.indexOf("&"))
    if (content == "") return "请告诉我你要加密的内容\n"
    if (info.endsWith("&")) return "请给我一个key\n"
    val key = info.substring(info.indexOf("&") + 2)
    if (key.length > 8 || key.toIntOrNull() == null) return "key应为1~8位纯数字\n"
    val url = "https://api.vvhan.com/api/jm?key=$key&string=$content&type=en"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return "无法获取body!\n"
        val jsonObject = JSON.parseObject(body)
        return jsonObject.getString("enmissString")
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时"
    } catch (e: Exception) {
        return "未知错误"
    }
}