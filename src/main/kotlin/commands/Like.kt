package org.ritsu.mirai.plugin.commands

/**
 * @author wcy
 * @param id QQ号
 * @return 返回信息, 图片路径
 */
fun like(id: Long): Pair<String, String?> {
    val url = "http://api.weijieyue.cn/api/tupian/zan.php?qq=$id"
    return try {
        val path = "./data/Image/like$id.jpg"
        val (code, msg) = download(url, path)
        if (code == 200 && msg == null) Pair("Success!", path)
        else Pair("Error: $code, $msg", null)
    } catch (e: java.net.SocketTimeoutException) {
        Pair("连接超时！\n", null)
    } catch (e: Exception) {
        Pair("未知错误！\n", null)
    }
}