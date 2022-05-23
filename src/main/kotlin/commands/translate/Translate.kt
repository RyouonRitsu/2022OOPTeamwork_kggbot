package org.ritsu.mirai.plugin.commands.translate

import com.alibaba.fastjson2.JSON

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

object NotAvailable {
    val groups = listOf(
        1L
    )
}

fun translate(query: String, lang: String = Lang.AUTO.desc): String {
    if (query == "") return "请输入要翻译的内容!"
    else if (lang == "") return "请输入要翻译成什么语言! 可省略->的部分, 默认使用自动检测!"
    val dstLang: String
    if (lang !in Lang.values().map { it.desc }) return "不支持的语言! 请使用\"kgg支持语言\"查看可供选择的语言列表! 可省略->的部分, 默认使用自动检测!"
    else dstLang = Lang.values().first { it.desc == lang }.code
    val appid = "20220509001208356"
    val securityKey = "wIeSpGbafIV9Yeh7ADSv"
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

fun languageType(): String {
    return Lang.values().joinToString(", ") { it.desc }
}