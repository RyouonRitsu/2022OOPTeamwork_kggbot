package org.ritsu.mirai.plugin.commands

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * @author wcy
 * @param loc 机场的ICAO代码
 * @return 查询METAR和TAF的结果
 */
fun getMetar(loc: String): String {
    if (loc == "") return "你要查询哪个机场呢，在后面加上它的ICAO代码吧~\n"
    val airport = loc.uppercase()
    val url = "https://aviationweather.gov/metar/data?ids=$airport&format=raw&date=&hours=0&taf=on&layout=on"
    val client = OkHttpClient().also { it.newBuilder().proxy(Proxy.NO_PROXY) }
    val request = Request.Builder().get().url(url).build()
    try {
        val response = client.newCall(request).execute()
        var body = response.body?.string() ?: return "无法获取body!\n"
        return when (body.indexOf("No METAR found")) {
            -1 -> {
                try {
                    if (body.indexOf(">$airport") == -1) return "No METAR found"
                    body = body.substring(body.indexOf(">$airport") + 1)
                    val metar = body.substring(0, body.indexOf("</code>"))
                    body = body.substring(body.indexOf("<br/>"))
                    var taf = body.substring(body.indexOf("<code>") + 6, body.indexOf("</code>"))
                        .replace("<br/>&nbsp;&nbsp;", "")
                    if (!taf.contains("TAF")) taf = "TAF $taf"
                    metar + "\n\n" + taf + "\n"
                } catch (e: Exception) {
                    "解析错误: ${e.message}\n"
                }
            }
            else -> "No METAR found"
        }
    } catch (e: java.net.SocketTimeoutException) {
        return "连接超时"
    } catch (e: Exception) {
        return "未知错误"
    }
}