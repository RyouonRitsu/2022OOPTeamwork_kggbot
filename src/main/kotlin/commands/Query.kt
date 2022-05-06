package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.contact.Friend
import org.ritsu.mirai.plugin.entity.User

fun queryStatus(sender: Friend): String {
    val user = User.getUser(sender)
    return "${user.account.nick}今天的能量值是${user.energyValue}\n已连续签到${user.signedCount}天"
}