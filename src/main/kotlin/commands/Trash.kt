package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param name 垃圾名称
 * @return 查询结果
 */
fun trash(name: String): String {
    if (name == "") return "请告诉我垃圾的名字\n"
    val url = "https://api.vvhan.com/api/la.ji?lj=$name"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return "无法获取body!\n"
        val jsonObject = JSON.parseObject(body)
        val msg = jsonObject.getString("sort")
        return if (msg == "俺也不知道是什么垃圾~") msg + "\n" else "${name}属于$msg\n"
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时"
    } catch (e: Exception) {
        return "未知错误"
    }
}