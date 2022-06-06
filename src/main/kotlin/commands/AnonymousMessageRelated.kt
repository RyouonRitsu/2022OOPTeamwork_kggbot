package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import org.ritsu.mirai.plugin.entity.AnonymousMessage
import org.ritsu.mirai.plugin.entity.User

/**
 * @author 卢嘉美-20373814
 * @version jdk15.0.2
 */
suspend fun sendAnonymousMessage(bot: Bot , sender:Friend, message: Message){
    val receiverstr = message.contentToString().
    replaceBefore("-" , "").replace("-" , "").
    replaceAfter("：" , "").replace("：" , "")
    val messageContent = message.contentToString().
    replaceBefore("：" , "").replaceFirst("：" , "")

    var receiverid : Long
    try {
        receiverid = receiverstr.toLong()
    } catch (e: NumberFormatException) {
        sender.sendMessage(PlainText("请输入正确的消息接受者ID"+receiverstr))
        return
    }
    val receiver = bot.getFriend(receiverid)
    if(receiver == null) sender.sendMessage("抱歉....对方不在我的联系范围内")
    else {
        val user = User.getUser(receiver)
        if(user.anonymousContact == 1) sender.sendMessage("抱歉....对方拒绝接收匿名消息")
        else{
            val number = AnonymousMessage.getRandom(bot)
            receiver.sendMessage(PlainText("您好，您收到一条编号为"+number+"的消息："+"\n"+messageContent+"\n"+"回复该消息请以——"
                +"Reply-"+number+"：——开头，如果不想再接收此类信息， 请回复td"))
            sender.sendMessage(PlainText("我已替您发送该消息,编号为"+number))
            val am = AnonymousMessage(number)
            am.receiverid = receiverid
            am.senderid = sender.id
            AnonymousMessage.messages.put(number , am)
            am.save()
        }
    }
}

suspend fun replyAnonymousMessage(bot: Bot, sender: Friend, message: Message){
    val numberStr = message.contentToString().
    replaceBefore("-" , "").replace("-" , "").
    replaceAfter("：" , "").replaceFirst("：" , "")
    val messageContent = message.contentToString().
    replaceBefore("：" , "").replace("：" , "")
    var number:Int
    try {
        number = numberStr.toInt()
    } catch (e: NumberFormatException) {
        sender.sendMessage(PlainText("请输入正确的消息编号"))
        return
    }
    val am = AnonymousMessage.release(number , sender.id)
    if(am != null){
        val receiver = bot.getFriend(am.senderid)
        receiver?.sendMessage(PlainText("您好，您的编号为"+number+"的消息已受到如下回复：\n" + messageContent))
        sender.sendMessage("我已替您回复该消息")
        am.delete()
    }
}

suspend fun accept(sender: Friend){
    val user = User(sender)
    user.anonymousContact = 0
    user.save()
    sender.sendMessage(PlainText("您已设置为允许接收匿名消息"))
}

suspend fun refuse(sender: Friend , bot: Bot){
    val user = User(sender)
    user.anonymousContact = 1
    user.save()
    sender.sendMessage(PlainText("您已设置为拒绝接收匿名消息"))
    AnonymousMessage.refuse(sender.id , bot)
}