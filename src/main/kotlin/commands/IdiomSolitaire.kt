package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson2.JSON
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy
import java.util.*

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
     * 记录当前进行游戏的群接龙的成语
     */
    val keyMap = HashMap<Long, LinkedList<String>>()
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
    val keyword = getPinYin(key)
    if (idiom == key) return Pair("这个重复了哦! 请换一个吧~ 当前接龙的字是\"$keyword\"!", null)
    val url = "https://api.iyk0.com/idiom/?msg=${idiom}&b=1"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        return when (response.code) {
            200 -> {
                val body = response.body?.string() ?: return Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是\"$keyword\"!", null)
                val msg = JSON.parseObject(body).getString("msg")
                if (msg == null && getPinYin(
                        idiom,
                        head = true
                    ) == keyword
                ) Pair("接龙成功! 下次一个成语请以\"${getPinYin(idiom)}\"开头~", idiom)
                else Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是\"$keyword\"!", null)
            }
            else -> Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是\"$keyword\"!", null)
        }
    } catch (e: Exception) {
        return Pair("这个不可以哦! 请换一个吧~ 当前接龙的字是\"$keyword\"!", null)
    }
}

/**
 * 获取词语首或尾的拼音
 *
 * @author RyouonRitsu
 * @param str 汉字
 * @param head 是否获取首字拼音
 * @return 拼音
 */
fun getPinYin(str: String, head: Boolean = false): String {
    val format = HanyuPinyinOutputFormat().apply {
        caseType = HanyuPinyinCaseType.LOWERCASE
        toneType = HanyuPinyinToneType.WITH_TONE_MARK
        vCharType = HanyuPinyinVCharType.WITH_U_UNICODE
    }
    return if (head) PinyinHelper.toHanYuPinyinString("${str[0]}", format, "", true)
    else PinyinHelper.toHanYuPinyinString("${str[str.length - 1]}", format, "", true)
}