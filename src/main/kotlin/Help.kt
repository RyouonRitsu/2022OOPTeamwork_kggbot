package org.ritsu.mirai.plugin

object Help {
    private val func: List<String> = listOf(
        "help", "抽卡", "我今天[<类型>?]吃什么[<类型>?][(x<2-10>)?]", "吃的类型", "mix(+)[<emoji>](+)[<emoji>](+)"
    )
    override fun toString(): String {
        var usage = "Usage -> {\n"
        for (it in func) usage += "\t$it\n"
        return "${usage}}\n使用方式 -> kgg[Usage]\nTips -> {\n\t[] 块内必须替换为相应语句\n\t<> 块内是变量描述\n\t? 代表可选参数\n" +
            "\t() 代表块内是一个整体或可选位置\n}\n更多新功能待开发, 欢迎提出宝贵建议uwu"
    }
}