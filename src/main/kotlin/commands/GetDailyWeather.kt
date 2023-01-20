package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param location 经纬度
 * @param city 城市名
 * @param day 第几天
 * @return 查询天气结果
 */
fun getDailyWeather(location: String, city: String, day: Int): String {
    val key = "******"
    val url = "https://devapi.qweather.com/v7/weather/3d?key=$key&location=$location"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    return when (response.code) {
        200 -> {
            try {
                val body = response.body?.string() ?: return "无法获取body!\n"
                val jsonObject = JSON.parseObject(body)
                if (jsonObject.getString("code") != "200") return "请求错误\n"
                val daily = jsonObject.getJSONArray("daily").getJSONObject(day)
                val fxDate = daily.getString("fxDate").substring(8)
                val sunrise = daily.getString("sunrise")
                val sunset = daily.getString("sunset")
                val moonrise = daily.getString("moonrise")
                val moonset = daily.getString("moonset")
                val moonPhase = daily.getString("moonPhase").replace("峨眉", "娥眉")
                val tempMax = daily.getString("tempMax")
                val tempMin = daily.getString("tempMin")
                val textDay = daily.getString("textDay")
                val textNight = daily.getString("textNight")
                val windDirDay = daily.getString("windDirDay")
                val windScaleDay = daily.getString("windScaleDay")
                val windSpeedDay = daily.getString("windSpeedDay")
                val windDirNight = daily.getString("windDirNight")
                val windScaleNight = daily.getString("windScaleNight")
                val windSpeedNight = daily.getString("windSpeedNight")
                val humidity = daily.getString("humidity")
                val precip = daily.getString("precip")
                val pressure = daily.getString("pressure")
                val vis = daily.getString("vis")
                val cloud = daily.getString("cloud")
                val uvIndex = daily.getString("uvIndex")
                var result = "$city${fxDate}日$tempMin~$tempMax°C。\n" +
                    "白天$textDay，$windDirDay${windScaleDay}级，风速${windSpeedDay}km/h，${sunrise}日出，${sunset}日落。\n" +
                    "夜间$textNight，$windDirNight${windScaleNight}级，风速${windSpeedNight}km/h，" +
                    "${moonrise}月出，${moonset}月落，$moonPhase。\n" +
                    "相对湿度$humidity%，当天总降水量${precip}mm，大气压${pressure}hPa，能见度${vis}km，紫外线强度$uvIndex"
                if (cloud != null && cloud != "") result += "，云量$cloud%"
                "$result。\n"
            } catch (e: Exception) {
                "解析错误: ${e.message}\n"
            }
        }
        403 -> "Incorrect or Invalid API Key! Please Edit Script to Configure...\n"
        else -> "Error: ${response.code}\n"
    }
}