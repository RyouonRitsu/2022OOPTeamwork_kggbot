package org.ritsu.mirai.plugin.commands.wordcloud

import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * 执行py脚本来构建群聊词云的词云功能
 *
 * @author RyouonRitsu
 * @param groupId 群号
 * @return 执行结果
 */
fun getGroupWordCloud(groupId: Long): String {
    var error: String? = null
    val result = runCatching {
        ProcessBuilder(listOf("python", "./group_wordcloud.py", "$groupId"))
            .directory(File("./src/main/kotlin/commands/wordcloud"))
            .redirectErrorStream(true)
            .start().also {
                if (!it.waitFor(30L, TimeUnit.SECONDS)) {
                    it.destroyForcibly()
                    if (it.isAlive) throw Exception("运行超时了, 但没杀掉进程!")
                    throw Exception("运行超时了!")
                }
            }
            .inputStream.bufferedReader(Charset.forName("GBK")).readText()
    }.onFailure { throwable -> throwable.message?.let { error = it } }.getOrNull()
    return if (error == null && result != null && "Prefix dict has been built successfully." in result) "Success"
    else "Error: $error\n$result"
}