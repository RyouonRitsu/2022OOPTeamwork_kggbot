package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.contact.Friend
import org.ritsu.mirai.plugin.entity.User

/**
 * 查询用户信息
 *
 * @author RyouonRitsu
 * @param sender 要查询的人
 * @return bot回复
 */
fun queryStatus(sender: Friend): String {
    val user = User.getUser(sender)
    return "${user.account.nick}现在有${user.energyValue}点能量值\n已连续签到${user.signedCount}天"
}