package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * 利用saucenao提供的Api进行图片搜索功能
 *
 * @author RyouonRitsu
 * @param imageUrl 要搜索的图片的url
 * @return Pair(错误信息, 本地保存图片地址)
 */
fun searchImageSource(imageUrl: String): Pair<String, String?> {
    val apiKey = "******"
    val minsim = "80!"
    val url =
        "https://saucenao.com/search.php?output_type=2&numres=1&testmode=1&minsim=$minsim&db=999&api_key=$apiKey&url=$imageUrl"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute()
    return when (response.code) {
        200 -> {
            try {
                val body = response.body?.string() ?: return Pair("无法获取body!\n", null)
                val jsonObject = JSON.parseObject(body)
                if (jsonObject.getJSONObject("header").getInteger("results_returned") > 0) {
                    val result = jsonObject.getJSONArray("results").getJSONObject(0)
                    val similarity = result.getJSONObject("header").getDouble("similarity")
                    val thumbnail = result.getJSONObject("header").getString("thumbnail")
                    val (code, msg) = download(thumbnail, "./data/Image/temp_thumbnail.png")
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
                } else Pair("no results... QAQ\n", null)
            } catch (e: Exception) {
                Pair("解析错误: ${e.message}\n", null)
            }
        }
        403 -> Pair("Incorrect or Invalid API Key! Please Edit Script to Configure...\n", null)
        else -> Pair("Error: ${response.code}\n", null)
    }
}

/**
 * 从指定url中下载文件到指定目录
 *
 * @author RyouonRitsu
 * @param url 要下载的文件的url
 * @param path 要保存到的文件目录
 * @param header 是否启用header
 * @param sa 指定代理
 * @return Pair(HTTP状态码, 错误信息)
 */
fun download(url: String, path: String, header: Boolean? = false, sa: InetSocketAddress? = null): Pair<Int, String?> {
    val client = OkHttpClient().also {
        it.newBuilder().apply {
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(20, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)
            if (sa != null) proxy(Proxy(Proxy.Type.HTTP, sa))
            else proxy(Proxy.NO_PROXY)
        }
    }
    val request = if (header == true)
        Request.Builder().get().url(url).addHeader("Referer", "no-referrer").build()
    else
        Request.Builder().get().url(url).build()
    try {
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
    } catch (e: java.net.SocketTimeoutException) {
        return Pair(408, "连接超时")
    } catch (e: Exception) {
        return Pair(500, "未知错误: ${e.message}")
    }
}