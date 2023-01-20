package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.*

/**
 * 使用@通知群成员
 *
 * @author ljm
 * @param message 通知消息的内容
 * @param group 需要进行通知的群
 */
suspend fun atMember(message: Message, group: Group) {
    var notice = message.contentToString()
    notice = if (notice.indexOf(":") > 0 && (notice.indexOf("：") < 0 || notice.indexOf(":") < notice.indexOf("：")))
        notice.replaceBefore(":", "").replaceFirst(":", "")
    else
        notice.replaceBefore("：", "").replaceFirst("：", "")
    val mem = message.contentToString().replaceFirst("!!!", "")
        .replaceAfter(":", "")
        .replaceAfter("：", "")
        .replace(":", "")
        .replace("：", "")
        .split(",", "，")
    var messAddAt: MessageChain = messageChainOf(PlainText("!!!"))
    for (userinfo in mem) {
        //group.sendMessage(PlainText(userinfo))
        for (user in group.members) {
            //group.sendMessage(PlainText(user.nameCardOrNick))
            val uname = user.nameCardOrNick
            if (uname.contains(userinfo.trim())) messAddAt += At(user)
        }
    }
    messAddAt += PlainText(":")
    messAddAt += PlainText(notice)
    group.sendMessage(messAddAt)
}

