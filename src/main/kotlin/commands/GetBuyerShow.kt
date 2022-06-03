package org.ritsu.mirai.plugin.commands

import okhttp3.OkHttpClient
import okhttp3.Request
import org.ritsu.mirai.plugin.entity.loadImage
import org.ritsu.mirai.plugin.entity.save
import java.io.File
import java.net.Proxy
import java.nio.charset.Charset

fun getBuyerShow(): Pair<String, String?> {
    val url = "http://api.weijieyue.cn/api/mjx/api.php"
    return try {
        val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
        val request = Request.Builder().get().url(url).build()
        val response = client.newCall(request).execute()
        val inputStream = response.body?.byteStream() ?: return Pair("未知错误！\n", null)
        val link = inputStream.bufferedReader(Charset.forName("UTF-8")).readText()
            .replace("±", "").replaceFirst("img=", "")
        val (code, msg) = download(link, "./data/Image/temp_buyershow.jpg")
        loadImage(File("./data/Image/temp_buyershow.jpg")).save(File("./data/Image/temp_buyershow.jpg"))
        if (code == 200 && msg == null) Pair("Success!", "./data/Image/temp_buyershow.jpg")
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}