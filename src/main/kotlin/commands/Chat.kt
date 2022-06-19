package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * 接入了小爱同学Api的聊天功能
 *
 * @author RyouonRitsu, wcy
 * @param msg 消息
 * @return 回复消息
 */
fun chat(msg: String): Pair<Boolean, String> {
//    val url = "https://api.iyk0.com/liaotian/?msg=$msg"
    val url = "http://api.weijieyue.cn/api/xiaoai/api.php?msg=$msg"
    val client = OkHttpClient().also {
        it.newBuilder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            proxy(Proxy.NO_PROXY)
        }
    }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return Pair(false, "呜呜呜！不知道该说什么了！")
        val jsonObject = JSON.parseObject(body)
        val content =
            jsonObject.getString("text")?.replace("小爱同学", "kgg")?.replace("小爱", "kgg")?.replace(",仁者爱人的＂爱＂.", "")
        if (content != null && content != "") return Pair(false, content)
        val mp3 = jsonObject.getString("mp3")
        val path = "./data/Image/temp_xiaoai.mp3"
        val (code, message) = download(mp3, path)
        return if (code == 200 && message == null) Pair(true, path)
        else Pair(false, "呜呜呜！不知道该说什么了！")
//        val inputStream = response.body?.byteStream() ?: return "呜呜呜！不知道该说什么了！"
//        return inputStream.bufferedReader(Charset.forName("UTF-8")).readText().replace("菲菲", "kgg")
    } catch (e: java.net.SocketTimeoutException) {
        return Pair(false, "呜呜呜！暂时不知道该说什么了！")
    } catch (e: Exception) {
        return Pair(false, "kgg出错了，重试一下吧！")
    }
}