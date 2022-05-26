package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request

fun getWeather(location: String): String {
    val key = "a3d192c29b9e448fabec91c966658079"
    val url = "https://devapi.qweather.com/v7/weather/now?key=$key&location=$location"
    val client = OkHttpClient()
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    return when (response.code) {
        200 -> {
            try {
                val body = response.body?.string() ?: return "无法获取body!\n"
                val jsonObject = JSON.parseObject(body)
                val temp = jsonObject.getJSONObject("now").getString("temp")
                val feelsLike = jsonObject.getJSONObject("now").getString("feelsLike")
                val text = jsonObject.getJSONObject("now").getString("text")
                val wind360 = jsonObject.getJSONObject("now").getString("wind360")
                val windDir = jsonObject.getJSONObject("now").getString("windDir")
                val windScale = jsonObject.getJSONObject("now").getString("windScale")
                val windSpeed = jsonObject.getJSONObject("now").getString("windSpeed")
                val humidity = jsonObject.getJSONObject("now").getString("humidity")
                val precip = jsonObject.getJSONObject("now").getString("precip")
                val pressure = jsonObject.getJSONObject("now").getString("pressure")
                val vis = jsonObject.getJSONObject("now").getString("vis")
                val cloud: String? = jsonObject.getJSONObject("now").getString("cloud")
                val dew: String? = jsonObject.getJSONObject("now").getString("dew")
                var result = "现在$text，气温$temp°C，体感温度$feelsLike°C，风向$wind360°，$windDir${windScale}级，" +
                    "风速${windSpeed}km/h，相对湿度$humidity%，当前小时累计降水量${precip}毫米，大气压${pressure}hPa，能见度${vis}km"
                if (cloud != null)
                    result += "，云量$cloud%"
                if (dew != null)
                    result += "，露点$dew°C"
                result
            } catch (e: Exception) {
                "解析错误: ${e.message}\n"
            }
        }
        403 -> "Incorrect or Invalid API Key! Please Edit Script to Configure...\n"
        else -> "Error: ${response.code}\n"
    }
}