package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MemberCardChangeEvent
import net.mamoe.mirai.message.data.*

suspend fun AtMember(message:Message, group:Group){
    var notice = message.contentToString().replaceBefore(":","").replaceFirst(":","");
    var mess_add_At : MessageChain
    var mem = message.contentToString().replaceFirst("!!!","")
        .replaceAfter(":","")
        .replace(":","")
        .split(',');
    mess_add_At = messageChainOf(PlainText("!!!"))
    for(userinfo in mem){
        group.sendMessage(PlainText(userinfo))
        for(user in group.members){
            group.sendMessage(PlainText(user.nameCardOrNick))
            var uname = user.nameCardOrNick
            if(uname.contains(userinfo)) mess_add_At += At(user)
        }
    }
    mess_add_At += PlainText(":")
    mess_add_At += PlainText(notice)
    group.sendMessage(mess_add_At)
}

