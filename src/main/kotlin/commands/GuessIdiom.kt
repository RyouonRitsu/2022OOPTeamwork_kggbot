package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * 猜成语功能，当返回结果的第一个值为true时表示运行成功，否则运行失败，此时可直接使用第二个值
 *
 * @author RyouonRitsu
 * @param justIdiom 是否只返回成语
 * @return Triple(是否成功, 成功时返回答案否则返回bot回复, 成功时返回图片地址否则返回null)
 */
fun guessIdiom(justIdiom: Boolean = false): Triple<Boolean, String, String?> {
    val tag = (0..1).random()
    val url = when (tag) {
        0 -> "http://api.weijieyue.cn/api/tupian/ktcy.php"
        else -> "https://api.iyk0.com/ktc/"
    }
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        return when (response.code) {
            200 -> {
                val body = response.body?.string() ?: return Triple(false, "呜呜, 我没想出题呢! 过会儿再试吧!", null)
                when (tag) {
                    0 -> {
                        val data = JSON.parseObject(body).getJSONArray("data")[0] as JSONObject
                        if (justIdiom) return Triple(true, data.getString("name"), null)
                        val (code, msg) = download(data.getString("image"), "./data/Image/idiom.jpg")
                        if (code == 200 && msg == null) Triple(true, data.getString("name"), "./data/Image/idiom.jpg")
                        else Triple(false, "呜呜, 我没想出题呢! 过会儿再试吧!", null)

                    }
                    else -> {
                        val jsonObject = JSON.parseObject(body)
                        if (justIdiom) return Triple(true, jsonObject.getString("key"), null)
                        val (code, msg) = download(jsonObject.getString("img"), "./data/Image/idiom.png")
                        if (code == 200 && msg == null) Triple(
                            true,
                            jsonObject.getString("key"),
                            "./data/Image/idiom.png"
                        )
                        else Triple(false, "呜呜, 我没想出题呢! 过会儿再试吧!", null)
                    }
                }
            }
            else -> Triple(false, "呜呜, 我没想出题呢! 过会儿再试吧!", null)
        }
    } catch (e: Exception) {
        return Triple(false, "呜呜, 我没想出题呢! 过会儿再试吧!", null)
    }
}