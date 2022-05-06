package org.ritsu.mirai.plugin

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.utils.BotConfiguration
import org.ritsu.mirai.plugin.kernel.loadData

suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    //如果是Kotlin
    PluginMain.load()
    PluginMain.enable()
    //如果是Java
//    JavaPluginMain.INSTANCE.load()
//    JavaPluginMain.INSTANCE.enable()

    val bot = MiraiConsole.addBot(3220063976, "zhx&LQS09071003") {
        fileBasedDeviceInfo()
        heartbeatStrategy = BotConfiguration.HeartbeatStrategy.REGISTER
        // 开启所有列表缓存
        enableContactCache()
    }.alsoLogin()

    loadData(bot.groups)

    MiraiConsole.job.join()
}