package org.ritsu.mirai.plugin.commands.translate

/**
 * 翻译Api模块
 *
 * @author RyouonRitsu
 * @since 0.1.0
 * @property appid 提供的appid
 * @property securityKey apiKey
 * @constructor 创建翻译Api模块
 */
class TransApi(private val appid: String, private val securityKey: String) {
    /**
     * 翻译文本
     *
     * @author RyouonRitsu
     * @param query 要翻译的文本
     * @param from 要翻译的语言
     * @param to 翻译成的语言
     * @return 翻译结果
     */
    fun getTransResult(query: String, from: String, to: String): String? {
        val params = buildParams(query, from, to)
        return HttpGet[TRANS_API_HOST, params]
    }

    /**
     * 构建查询翻译的网址的params
     *
     * @author RyouonRitsu
     * @param query 要翻译的文本
     * @param from 要翻译的语言
     * @param to 翻译成的语言
     * @return 翻译网址的params
     */
    private fun buildParams(query: String, from: String, to: String): Map<String?, String?> {
        val params: MutableMap<String?, String?> = HashMap()
        params["q"] = query
        params["from"] = from
        params["to"] = to
        params["appid"] = appid

        // 随机数
        val salt = System.currentTimeMillis().toString()
        params["salt"] = salt

        // 签名
        val src = appid + query + salt + securityKey // 加密前的原文
        params["sign"] = MD5.md5(src)
        return params
    }

    companion object {
        /**
         * 翻译Api网址
         */
        private const val TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate"
    }
}