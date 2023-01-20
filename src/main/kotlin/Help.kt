package org.ritsu.mirai.plugin

/**
 * 这是一个帮助单例，用于获取kggbot的可供用户使用的帮助信息
 *
 * @author RyouonRitsu
 * @since 0.1.0
 */
object Help {
    /**
     * 用户群聊可用命令列表，每次更新命令需在此添加
     */
    val funcGroup: List<String> = listOf(
        "help", "抽卡", "我今天[<类型>?]吃什么[<类型>?][(x<2~10>)?]", "mix[<emoji>][<emoji>]", "python[<命令脚本>]",
        "dice[<大于1的整数>]", "占卜一下[<事件>]", "t[<内容>][(-><语言>)?]", "支持语言", "搜图[<一张或多张图片>?]", "吃的类型",
        "今日词云", "舔[<@一位用户>]", "metar[<机场ICAO代码>]", "摸鱼", "二维码[<二维码的内容>]", "陪我聊天", "cos", "美女",
        "cat", "news", "来点[<mix>?][<r18>?][<tag，多个tag用'&'连接>?]", "文章[<内容>][<&字数>?]", "en[<待加密内容>][<&&key>]",
        "de[<待解密内容>][<&key>]", "[<垃圾名>]是什么垃圾", "[<城市>?][<明天/后天>?]天气[<定位>?]", "爬[<@一位用户>]", "买家秀",
        "赞[<@一位用户>]", "丢[<@一位用户>]", "双色球[<双色球号码>]", "[<中国内地省级行政区/全国>]油价[<文字>]", "猜成语", "成语接龙",
        "支持语言"
    )

    /**
     * 用户私聊可用命令列表，每次更新命令需在此添加
     */
    val funcFriend = listOf(
        "help", "查询状态", "dice[<大于1的整数>]", "cos", "cat", "陪我聊天", "news", "买家秀", "二维码[<二维码的内容>]",
        "metar[<机场ICAO代码>]", "来点[<mix>?][<r18>?][<tag，多个tag用'&'连接>?]", "文章[<内容>][<&字数>?]", "摸鱼", "美女",
        "en[<待加密内容>][<&&key>]", "de[<待解密内容>][<&key>]", "[<垃圾名>]是什么垃圾", "[<城市>?][<明天/后天>?]天气[<定位>?]",
        "申请解除禁言 [需要解禁的群号] [新群名片]", "匿名消息-[接收方id]：[发送的信息]", "Reply-[信息编号]：", "双色球[<双色球号码>]",
        "[<中国内地省级行政区/全国>]油价[<文字>]", "我今天[<类型>?]吃什么[<类型>?][(x<2~10>)?]", "mix[<emoji>][<emoji>]",
        "占卜一下[<事件>]", "python[<命令脚本>]", "吃的类型", "支持语言"
    )

    val funcTemp: List<String> = listOf(
        "申请解除禁言 [新群名片]"
    )

    /**
     * 获取帮助信息
     *
     * @author RyouonRitsu
     * @param list 函数列表，可选Help单例中的funcGroup或funcFriend
     * @return 帮助信息
     */
    fun toString(list: List<String>): String {
        var usage = "Usage -> {\n"
        for (it in list) usage += "    $it\n"
        return "${usage}}\n使用方式 -> {\n    群聊 -> {\n        kgg[Usage]\n        也可以直接@kgg与他聊天\n    }\n" +
            "    私聊 -> [Usage]\n}\nTips -> {\n    [] 块内必须替换为相应语句\n    <> 块内是变量描述\n    ? 代表可选参数\n" +
            "    () 代表块内是一个整体或可选位置\n}\n签到方式 -> {\n    kgg抽卡(倍率1.0 + 幸运值)\n    kgg(倍率1.0)\n    跟bot私聊任何消息(倍率1.0)\n}\n" +
            "能量值的获取量除了会乘上倍率还会和连续签到的天数相关哦!\n更多新功能待开发, 欢迎提出宝贵建议uwu"
    }

    /**
     * 搜索用户可能要使用的命令
     *
     * @author RyouonRitsu
     * @param cmd 用户输入的命令
     * @param funcList 使用场景下的命令列表，可选Help单例中的funcGroup或funcFriend
     * @return 命令信息
     */
    fun searchCommand(cmd: String, funcList: List<String>): String? {
        for (index in (cmd.length - 1) downTo 0) {
            funcList.forEach {
                if (cmd.substring(0..index) in it) return "您是否想用\"${if (funcList == funcGroup) "kgg" else ""}$it\"?"
            }
        }
        return null
    }
}