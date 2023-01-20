package org.ritsu.mirai.plugin.commands

import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * 执行python脚本，依赖服务器本机的python环境
 *
 * @author RyouonRitsu
 * @param code 脚本代码
 * @return Pair(脚本执行结果, 错误信息)
 */
fun runPython(code: String): Pair<String?, String?> {
    val file = File("./data/cmd.py")
    file.writeText(
        code.replaceFirst("python", "")
            .replace("exec", "")
            .replace("eval", "")
            .replace("__import__", "")
            .replace("sys", "")
            .replace("os", "")
            .replace("argparse", "")
            .replace("threading", "")
            .replace("multiprocessing", "")
            .replace("subprocess", "")
            .replace("input", "")
    )
    var error: String? = null
    val result = runCatching {
        ProcessBuilder(listOf("python", "./cmd.py"))
            .directory(File("./data"))
            .redirectErrorStream(true)
            .start().also {
                if (!it.waitFor(20L, TimeUnit.SECONDS)) {
                    it.destroyForcibly()
                    if (it.isAlive) throw Exception("运行超时了, 但没杀掉进程!")
                    throw Exception("运行超时了!")
                }
            }
            .inputStream.bufferedReader(Charset.forName("GBK")).readText()
    }.onFailure { throwable -> throwable.message?.let { error = it } }.getOrNull()
    return Pair(result, error)
}