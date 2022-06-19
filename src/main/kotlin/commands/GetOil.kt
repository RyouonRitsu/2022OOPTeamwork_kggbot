package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param loc 省级行政区的名称
 * @return 返回信息, 查询结果
 */
fun getOil(loc: String): String {
    if (loc == "") return "请输入中国内地省级行政区！\n"
    val url = "https://api.iyk0.com/youjia"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return "无法获取body!\n"
        if (loc == "全国") {
            val jsonObject = JSON.parseObject(body)
            var i = 0
            var result = ""
            while (i < 31) {
                val region = jsonObject.getJSONArray("data").getJSONObject(i).getString("地区")
                val obj = jsonObject.getJSONArray("data").getJSONObject(i)
                val o89: String? = obj.getString("89号汽油")
                val o92: String? = obj.getString("92号汽油")
                val o95: String? = obj.getString("95号汽油")
                val o98: String? = obj.getString("98号汽油")
                val o0: String? = obj.getString("0号柴油")
                result += "${region}油价：\n"
                if (o89 != null && !o89.startsWith("0")) result += "89号汽油：$o89\n"
                if (o92 != null && !o92.startsWith("0")) result += "92号汽油：$o92\n"
                if (o95 != null && !o95.startsWith("0")) result += "95号汽油：$o95\n"
                if (o98 != null && !o98.startsWith("0")) result += "98号汽油：$o98\n"
                if (o0 != null && !o0.startsWith("0")) result += "0号柴油：$o0\n"
                result += "\n"
                i++
            }
            return result + "更新日期：" + jsonObject.getJSONArray("data").getJSONObject(0).getString("更新日期") + "\n"
        }
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
                        return "地区不正确，请输入中国内地省级行政区！\n"
                    }
                }
                if (!flag) return "地区不正确，请输入中国内地省级行政区！\n"
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
                result
            }
            else -> "Error: ${response.code}\n"
        }
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时！\n"
    } catch (e: Exception) {
        return "未知错误！\n"
    }
}