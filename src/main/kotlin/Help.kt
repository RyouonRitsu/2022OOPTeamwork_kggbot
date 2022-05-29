package org.ritsu.mirai.plugin

object Help {
    val funcGroup: List<String> = listOf(
        "help", "抽卡", "我今天[<类型>?]吃什么[<类型>?][(x<2~10>)?]", "吃的类型", "mix(+)[<emoji>](+)[<emoji>](+)",
        "dice[<大于1的整数>]", "占卜一下[<事件>]", "t[<内容>][(-><语言>)?]", "支持语言", "搜图[<一张或多张图片>?]", "Python",
        "今日词云", "舔[<@一位用户>]", "metar[<机场ICAO代码>]", "天气[<定位>]", "[<中国内地省级行政区>]油价", "陪我聊天", "cos",
        "cat",
    )

    val funcFriend = listOf(
        "help", "查询状态", "dice[<大于1的整数>]", "cos", "cat", "陪我聊天"
    )

    fun toString(list: List<String>): String {
        var usage = "Usage -> {\n"
        for (it in list) usage += "\t$it\n"
        return "${usage}}\n使用方式 -> {\n\t群聊 -> kgg[Usage]\n\t私聊 -> [Usage]\n}\nTips -> {\n\t[] 块内必须替换为相应语句\n\t<> 块内是变量描述\n\t? 代表可选参数\n" +
            "\t() 代表块内是一个整体或可选位置\n}\n签到方式 -> {\n\tkgg抽卡(倍率1.0 + 幸运值)\n\tkgg(倍率1.0)\n\t跟bot私聊任何消息(倍率1.0)\n}\n" +
            "能量值的获取量除了会乘上倍率还会和连续签到的天数相关哦!\n更多新功能待开发, 欢迎提出宝贵建议uwu"
    }
}