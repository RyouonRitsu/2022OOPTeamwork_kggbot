package org.ritsu.mirai.plugin.commands.translate

import com.alibaba.fastjson2.JSON

/**
 * 开放给用户的语言枚举类
 *
 * @author RyouonRitsu
 * @since 0.1.0
 * @property code 语言代号
 * @property desc 语言名称描述
 */
enum class Lang(val code: String, val desc: String) {
    AUTO("auto", "自动检测"),
    ENGLISH("en", "英语"),
    CHINESE("zh", "中文"),
    CHINESE_T("cht", "繁体中文"),
    JAPANESE("jp", "日语"),
    YUEYU("yue", "粤语"),
    KOREAN("kor", "韩语"),
    WYW("wyw", "文言文"),
    FRENCH("fra", "法语"),
    GERMAN("de", "德语"),
    RUSSIAN("ru", "俄语"),
    ITALIAN("it", "意大利语")
}

/**
 * 禁止使用翻译功能的群列表
 *
 * @author RyouonRitsu
 * @since 0.1.0
 */
object NotAvailable {
    /**
     * 此处陈列需要禁止使用翻译的群
     */
    val groups = listOf(
        1L
    )
}

/**
 * 翻译功能
 *
 * @author RyouonRitsu
 * @param query 翻译的查询内容
 * @param lang 翻译的目标语言
 * @return 翻译结果
 */
fun translate(query: String, lang: String = Lang.AUTO.desc): String {
    if (query == "") return "请输入要翻译的内容!"
    else if (lang == "") return "请输入要翻译成什么语言! 可省略->的部分, 默认使用自动检测!"
    val dstLang: String
    if (lang !in Lang.values().map { it.desc }) return "不支持的语言! 请使用\"kgg支持语言\"查看可供选择的语言列表! 可省略->的部分, 默认使用自动检测!"
    else dstLang = Lang.values().first { it.desc == lang }.code
    val appid = "******"
    val securityKey = "******"
    val api = TransApi(appid, securityKey)
    val result = api.getTransResult(query, "auto", dstLang)
    return if (result == null) "翻译失败!"
    else {
        val jsonObject = JSON.parseObject(result)
        if (jsonObject.containsKey("trans_result")) {
            val jsonArr = jsonObject.getJSONArray("trans_result")
            return jsonArr.getJSONObject(0).getString("dst")
        } else "出错了! TraceBack:\n$result"
    }
}

/**
 * 获取目前支持的翻译语言
 *
 * @author RyouonRitsu
 * @return 目前支持的翻译语言
 */
fun languageType(): String {
    return Lang.values().joinToString(", ") { it.desc }
}