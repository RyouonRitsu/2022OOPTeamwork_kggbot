package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

fun searchImageSource(imageUrl: String): Pair<String, String?> {
    val apiKey = "d9c7172f1cf935901106e36af76f3c469505f225"
    val minsim = "80!"
    val url =
        "https://saucenao.com/search.php?output_type=2&numres=1&testmode=1&minsim=$minsim&db=999&api_key=$apiKey&url=$imageUrl"
    val client = OkHttpClient()
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    return when (response.code) {
        200 -> {
            try {
                val body = response.body?.string() ?: return Pair("无法获取body!", null)
                val jsonObject = JSON.parseObject(body)
                if (jsonObject.getJSONObject("header").getInteger("results_returned") > 0) {
                    val result = jsonObject.getJSONArray("results").getJSONObject(0)
                    val similarity = result.getJSONObject("header").getDouble("similarity")
                    val thumbnail = result.getJSONObject("header").getString("thumbnail")
                    val (code, msg) = downloadPicture(thumbnail, "./data/Image/temp_thumbnail.png")
                    var string = ""
                    result.getJSONObject("data").forEach {
                        if (it.value != null) {
                            string += if (it.key == "ext_urls") "链接: " else "${it.key.toString().replace("_", " ")}: "
                            string += "${
                                it.value.toString().replace("[", "").replace("]", "")
                                    .replace("\"", "").replace(",", ", ")
                            }\n"
                        }
                    }
                    Pair(
                        "相似度: $similarity%\n$string",
                        if (code == 200 && msg == null) "./data/Image/temp_thumbnail.png" else msg
                    )
                } else Pair("no results... QAQ", null)
            } catch (e: Exception) {
                Pair("解析错误: ${e.message}", null)
            }
        }
        403 -> Pair("Incorrect or Invalid API Key! Please Edit Script to Configure...", null)
        else -> Pair("Error: ${response.code}", null)
    }
}

fun downloadPicture(url: String, path: String): Pair<Int, String?> {
    val client = OkHttpClient()
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    when (response.code) {
        200 -> {
            val inputStream = response.body?.byteStream() ?: return Pair(response.code, "无法获取body!")
            return try {
                val fos: FileOutputStream
                val file = File(path)
                fos = FileOutputStream(file)
                fos.write(inputStream.readBytes())
                fos.flush()
                fos.close()
                Pair(response.code, null)
            } catch (e: Exception) {
                Pair(response.code, "写入文件错误: ${e.message}")
            }
        }
        else -> return Pair(response.code, "Error: ${response.code}")
    }
}