package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import org.ritsu.mirai.plugin.entity.Grp

/**
 * 禁言群名片不符合要求的群成员
 *
 * @author ljm
 * @param message 群管理员发送的进行禁言相关群成员操作的消息
 * @param group 进行管理的群
 */
suspend fun mute(message: Message, group: Group) {
    val mes = message.contentToString().split(" ")
    val formatStr = mes[1]
    val format: Regex? = if (formatStr == "空") null
    else mes[1].toRegex()
    var exception = 0
    var len = 0
    try {
        val lenstr = mes[2]
        len = lenstr.toInt()
    } catch (e: NumberFormatException) {
        exception = 1
        group.sendMessage(PlainText("格式不正确,请确保禁言时间为整数"))
    } catch (e: IndexOutOfBoundsException) {
        len = 10 * 24 * 60 * 60
    }
    if (exception == 0) {
        val grp = Grp.getGroup(group)
        if (format != null) {
            grp.format = format.toString()
            grp.save()
        }
        if (grp.format == null) group.sendMessage(PlainText("请设置群名片格式"))
        else {
            for (mem in group.members) {
                if (mem.permission == MemberPermission.ADMINISTRATOR || mem.permission == MemberPermission.OWNER) continue
                if (!mem.nameCardOrNick.matches(grp.format!!.toRegex())) {
                    //group.sendMessage(PlainText(mem.nameCardOrNick))
                    if (!mem.isMuted && grp.mutelist.indexOf(mem.id) == -1) grp.mutelist.add(mem.id)
                    mem.mute(len)
                }
            }
            grp.save()
        }
    }
}

/**
 * 被禁言的群成员申请解禁
 *
 * @author ljm
 * @param bot 对应的群聊机器人
 * @param newname 申请解禁言的新群名片
 * @param groupstr 申请解禁言的群号字符串
 * @param mem 申请解禁言的用户
 * @return bot回复
 */
suspend fun unmute(bot: Bot, newname: String, groupstr: String, mem: Long): String {
    val groupid: Long
    try {
        groupid = groupstr.toLong()
    } catch (e: NumberFormatException) {
        return "请输入正确的群号"
    }
    val group = bot.getGroup(groupid) ?: return "请输入正确的群号"
    val member = group[mem] ?: return "请输入正确的群号"
    if (!member.isMuted) return "你本来就可以说话啊"
    val grp = Grp.getGroup(group)
    return if (grp.format == null || newname.matches(grp.format!!.toRegex())) {
        val index = grp.mutelist.indexOf(mem)
        if (index != -1) {
            grp.mutelist.removeAt(index)
            grp.save()
            member.unmute()
            member.nameCard = newname
            "你已被解除禁言"
        } else "抱歉，我无权解禁"
    } else "你的群名片不正确，请修改后重新申请"
}

/**
 * 清除群名片不符合要求的群成员
 *
 * @author ljm
 * @param group 进行管理的群
 * @param message 群管理员发送的进行禁言相关群成员操作的消息
 */
suspend fun kick(group: Group, message: Message) {
    val mes = message.contentToString().split(" ")
    val formatStr = mes[1]
    val format: Regex?
    val grp = Grp.getGroup(group)
    if (formatStr == "空") format = grp.format!!.toRegex()
    else {
        format = formatStr.toRegex()
        grp.format = formatStr
        grp.save()
    }
    if (format == null) {
        group.sendMessage(PlainText("请先设置群名片格式"))
        return
    } else {
        //group.sendMessage(PlainText(format.toString()))
        for (mem in group.members) {
            if (mem.permission == MemberPermission.OWNER || mem.permission == MemberPermission.ADMINISTRATOR) continue
            if (!mem.nameCardOrNick.matches(format)) {
                mem.kick("你的群名片不符合修改要求")
            }
        }
    }
}