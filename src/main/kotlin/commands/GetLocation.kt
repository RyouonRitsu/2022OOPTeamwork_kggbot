package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param loc 查询地点名称
 * @return 返回信息, 经纬度
 */
fun getLocation(loc: String): Pair<String, String?> {
    val key = "******"
    val url = "https://api.map.baidu.com/place/v2/search?query=$loc&region=$loc&output=json&ak=$key"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
        return when (response.code) {
            200 -> {
                val jsonObject = JSON.parseObject(body)
                val location = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("location")
                val lat = location.getString("lat")
                val lng = location.getString("lng")
                if (lat != null && lng != null) Pair("success", "$lng,$lat")
                else Pair("抱歉，没找到这个位置\n", null)
            }
            else -> Pair("Error: ${response.code}\n", null)
        }
    } catch (e: java.net.SocketTimeoutException) {
        return Pair("连接超时！\n", null)
    } catch (e: Exception) {
        return Pair("未知错误！\n", null)
    }
}