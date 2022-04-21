package org.ritsu.mirai.plugin

object Help {
    val func: List<String> = listOf("help", "抽卡")
    override fun toString(): String {
        var usage = "Usage:\n"
        for (it in func) {
            usage += "\t$it: 使用方式为kgg$it\n"
        }
        return "$usage\t新功能待开发..."
    }

}