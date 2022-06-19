package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import org.ritsu.mirai.plugin.entity.AnonymousMessage
import org.ritsu.mirai.plugin.entity.User

/**
 * 申请发送匿名消息
 *
 * @author ljm
 * @param bot 对应的群聊机器人
 * @param sender 申请发送匿名消息的用户
 * @param message 消息内容
 */
suspend fun sendAnonymousMessage(bot: Bot, sender: Friend, message: Message) {
    val receiverstr =
        message.contentToString().replaceBefore("-", "").replace("-", "").replaceAfter("：", "").replace("：", "")
    val messageContent = message.contentToString().replaceBefore("：", "").replaceFirst("：", "")

    val receiverid: Long
    try {
        receiverid = receiverstr.toLong()
    } catch (e: NumberFormatException) {
        sender.sendMessage(PlainText("请输入正确的消息接受者ID$receiverstr"))
        return
    }
    val receiver = bot.getFriend(receiverid)
    if (receiver == null) sender.sendMessage("抱歉....对方不在我的联系范围内")
    else {
        val user = User.getUser(receiver)
        if (user.blockAnonymousMessage == true) sender.sendMessage("抱歉....对方拒绝接收匿名消息")
        else {
            val number = AnonymousMessage.getRandom(bot)
            receiver.sendMessage(
                PlainText(
                    "您好，您收到一条编号为" + number + "的消息：" + "\n" + messageContent + "\n" + "回复该消息请以——"
                        + "Reply-" + number + "：——开头，如果不想再接收此类信息， 请回复td"
                )
            )
            sender.sendMessage(PlainText("我已替您发送该消息,编号为$number"))
            val am = AnonymousMessage(number)
            am.receiverid = receiverid
            am.senderid = sender.id
            AnonymousMessage.messages[number] = am
            am.save()
        }
    }
}

/**
 * 回复匿名消息
 *
 * @author ljm
 * @param bot 对应的群聊机器人
 * @param sender 申请回复匿名消息的用户
 * @param message 消息内容
 */
suspend fun replyAnonymousMessage(bot: Bot, sender: Friend, message: Message) {
    val numberStr =
        message.contentToString().replaceBefore("-", "").replace("-", "").replaceAfter("：", "").replaceFirst("：", "")
    val messageContent = message.contentToString().replaceBefore("：", "").replace("：", "")
    val number: Int
    try {
        number = numberStr.toInt()
    } catch (e: NumberFormatException) {
        sender.sendMessage(PlainText("请输入正确的消息编号"))
        return
    }
    val am = AnonymousMessage.release(number, sender.id)
    if (am != null) {
        val receiver = bot.getFriend(am.senderid)
        receiver?.sendMessage(PlainText("您好，您的编号为" + number + "的消息已受到如下回复：\n" + messageContent))
        sender.sendMessage("我已替您回复该消息")
        am.delete()
    }
}


/**
 * 设置为允许接收匿名消息
 *
 * @author ljm
 * @param sender 进行设置的用户
 */
suspend fun accept(sender: Friend) {
    val user = User.getUser(sender)
    user.blockAnonymousMessage = false
    user.save()
    sender.sendMessage(PlainText("您已设置为允许接收匿名消息"))
}

/**
 * 设置为拒绝接收匿名消息
 *
 * @author ljm
 * @param sender 进行设置的用户
 * @param bot 对应的群聊机器人
 */
suspend fun refuse(sender: Friend, bot: Bot) {
    val user = User.getUser(sender)
    user.blockAnonymousMessage = true
    user.save()
    sender.sendMessage(PlainText("您已设置为拒绝接收匿名消息"))
    AnonymousMessage.refuse(sender.id, bot)
}