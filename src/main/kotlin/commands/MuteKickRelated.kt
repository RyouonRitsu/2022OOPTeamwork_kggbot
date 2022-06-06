package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import org.ritsu.mirai.plugin.entity.Grp

/**
 * @author 卢嘉美-20373814
 * @version jdk15.0.2
 */
suspend fun mute(message: Message, group: Group) {
    val mes = message.contentToString().split(" ")
    val formatStr = mes[1]
    val format:Regex?
    if(formatStr.equals("空")) format = null
    else format = mes[1].toRegex()
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
    if(exception == 0) {
        val grp = Grp.getGroup(group)
        if(format != null) {
            grp.format = format
            grp.save()
        }
        if(grp.format == null) group.sendMessage(PlainText("请设置群名片格式"))
        else {
            for (mem in group.members) {
                if (mem.permission == MemberPermission.ADMINISTRATOR || mem.permission == MemberPermission.OWNER) continue
                if (!mem.nameCardOrNick.matches(grp.format!!)) {
                    //group.sendMessage(PlainText(mem.nameCardOrNick))
                    if(!mem.isMuted) grp.mutelist.add(mem.id)
                    mem.mute(len)
                }
            }
            grp.save()
        }
    }
}

suspend fun unmute(bot : Bot , newname: String , groupstr: String , mem: Long) : String{
    var groupid : Long
    try{
       groupid = groupstr.toLong()
    }catch (e: NumberFormatException) {
        return "请输入正确的群号"
    }
    val group = bot.getGroup(groupid)
    if(group == null) return "请输入正确的群号"
    val member = group.get(mem)
    if(member == null) return "请输入正确的群号"
    if(!member.isMuted) return "你本来就可以说话啊"
    val grp = Grp.getGroup(group)
    if(grp.format == null || newname.matches(grp.format!!))  {
        val index = grp.mutelist.indexOf(mem)
        if(index != -1) {
            grp.mutelist.removeAt(index)
            grp.save()
            member.unmute()
            member.nameCard = newname
            return "你已被解除禁言"
        }
        else return "抱歉，我无权解禁"
    }
    else return "你的群名片不正确，请修改后重新申请"
}

suspend fun kick(group : Group, message: Message){
    val mes = message.contentToString().split(" ")
    val formatStr = mes[1]
    val format:Regex?
    val grp = Grp.getGroup(group)
    if(formatStr.equals("空")) format = grp.format
    else {
        format = formatStr.toRegex()
        grp.format = format
        grp.save()
    }
    if(format == null) {
        group.sendMessage(PlainText("请先设置群名片格式"))
        return
    }
    else{
        //group.sendMessage(PlainText(format.toString()))
        for (mem in group.members) {
            if (mem.permission == MemberPermission.OWNER || mem.permission == MemberPermission.ADMINISTRATOR) continue
            if (!mem.nameCardOrNick.matches(format)) {
                mem.kick("你的群名片不符合修改要求")
            }
        }
    }
}