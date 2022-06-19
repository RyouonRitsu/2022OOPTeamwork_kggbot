package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param location 经纬度
 * @param city 城市名
 * @return 查询天气结果
 */
fun getNowWeather(location: String, city: String? = null): String {
    val key = "******"
    val url = "https://devapi.qweather.com/v7/weather/now?key=$key&location=$location"
    val client = OkHttpClient().also {
        it.newBuilder().proxy(Proxy.NO_PROXY)
    }
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    return when (response.code) {
        200 -> {
            try {
                val body = response.body?.string() ?: return "无法获取body!\n"
                val jsonObject = JSON.parseObject(body)
                if (jsonObject.getString("code") != "200") return "请求错误\n"
                val now = jsonObject.getJSONObject("now")
                val temp = now.getString("temp")
                val feelsLike = now.getString("feelsLike")
                val text = now.getString("text")
                val wind360 = now.getString("wind360")
                val windDir = now.getString("windDir")
                val windScale = now.getString("windScale")
                val windSpeed = now.getString("windSpeed")
                val humidity = now.getString("humidity")
                val precip = now.getString("precip")
                val pressure = now.getString("pressure")
                val vis = now.getString("vis")
                val cloud: String? = now.getString("cloud")
                val dew: String? = now.getString("dew")
                var result = "现在$text，气温$temp°C，体感温度$feelsLike°C，风向$wind360°，$windDir${windScale}级，" +
                    "风速${windSpeed}km/h，相对湿度$humidity%，当前小时累计降水量${precip}毫米，大气压${pressure}hPa，能见度${vis}km"
                if (cloud != null && cloud != "") result += "，云量$cloud%"
                if (dew != null && dew != "") result += "，露点$dew°C"
                if (city != null) result = city + result
                "$result。\n"
            } catch (e: Exception) {
                "解析错误: ${e.message}\n"
            }
        }
        403 -> "Incorrect or Invalid API Key! Please Edit Script to Configure...\n"
        else -> "Error: ${response.code}\n"
    }
}