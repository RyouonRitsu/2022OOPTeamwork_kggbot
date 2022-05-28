package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.*

suspend fun atMember(message: Message, group: Group) {
    val notice = message.contentToString().replaceBefore(":", "").replaceFirst(":", "")
    val mem = message.contentToString().replaceFirst("!!!", "")
        .replaceAfter(":", "")
        .replace(":", "")
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

