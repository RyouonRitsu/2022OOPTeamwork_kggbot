package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * 存放成语接龙的所需数据
 *
 * @author RyouonRitsu
 * @since 0.1.0
 */
object IdiomSolitaire {
    /**
     * 记录当前开始游戏的群号
     */
    val gameMap = HashMap<Long, Boolean>()

    /**
     * 记录当前进行游戏的群正在接龙的成语
     */
    val keyMap = HashMap<Long, String>()
}

/**
 * 成语接龙功能
 *
 * @author RyouonRitsu
 * @param idiom 成语
 * @param key 正在接龙的成语
 * @return Pair(bot回复, 下一个接龙的成语)
 */
fun idiomSolitaire(idiom: String, key: String): Pair<String, String?> {
    val keyword = key[key.length - 1]
    if (idiom == key) return Pair("这个重复了哦! 请换一个吧~ 当前接龙的字是$keyword", null)
    val url = "https://api.iyk0.com/idiom/?msg=${idiom}&b=1"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        return when (response.code) {
            200 -> {
                val body = response.body?.string() ?: return Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是$keyword", null)
                val msg = JSON.parseObject(body).getString("msg")
                if (msg == null && idiom[0] == keyword) Pair("接龙成功! 下次一个成语请以\"${idiom[idiom.length - 1]}\"开头~", idiom)
                else Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是$keyword", null)
            }
            else -> Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是$keyword", null)
        }
    } catch (e: Exception) {
        return Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是$keyword", null)
    }
}