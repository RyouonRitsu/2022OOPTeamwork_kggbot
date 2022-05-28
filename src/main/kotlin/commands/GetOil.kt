package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

fun getOil(loc: String): Pair<String, String?> {
    if (loc == "") return Pair("请输入中国内地省级行政区！\n", null)
    val url = "https://api.iyk0.com/youjia"
    val client = OkHttpClient().also {
        it.newBuilder().apply {
            proxy(Proxy.NO_PROXY)
        }
    }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
        return when (response.code) {
            200 -> {
                val jsonObject = JSON.parseObject(body)
                var i = 0
                var flag = false
                while (i < 31) {
                    try {
                        val region = jsonObject.getJSONArray("data").getJSONObject(i).getString("地区")
                        if (region == loc) {
                            flag = true
                            break
                        }
                        i++
                    } catch (e: Exception) {
                        return Pair("地区不正确，请输入中国内地省级行政区！\n", null)
                    }
                }
                if (!flag) return Pair("地区不正确，请输入中国内地省级行政区！\n", null)
                val obj = jsonObject.getJSONArray("data").getJSONObject(i)
                val o89: String? = obj.getString("89号汽油")
                val o92: String? = obj.getString("92号汽油")
                val o95: String? = obj.getString("95号汽油")
                val o98: String? = obj.getString("98号汽油")
                val o0: String? = obj.getString("0号柴油")
                val date: String? = obj.getString("更新日期")
                var result = "${loc}油价：\n"
                if (o89 != null && !o89.startsWith("0")) result += "89号汽油：$o89\n"
                if (o92 != null && !o92.startsWith("0")) result += "92号汽油：$o92\n"
                if (o95 != null && !o95.startsWith("0")) result += "95号汽油：$o95\n"
                if (o98 != null && !o98.startsWith("0")) result += "98号汽油：$o98\n"
                if (o0 != null && !o0.startsWith("0")) result += "0号柴油：$o0\n"
                if (date != null) result += "更新日期：$date\n"
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