package org.ritsu.mirai.plugin.entity

import net.mamoe.mirai.contact.nameCardOrNick
import org.ritsu.mirai.plugin.kernel.addEnergy

object Administrator {
    val administrators: List<Long> = listOf(
        1780645196L,
        75046675L,
        2287941423L
    )
    val botList: List<Long> = listOf(
        1784958674L,
        3110526590L
    )
    val blacklist = ArrayList<Long>()
}

fun adjustUserEnergy(target: Long?, amount: Int?): String {
    val energy: Int?
    return if (target != null) {
        energy = User.users[target]?.energyValue
        if (energy != null) {
            if (amount != null) {
                addEnergy(User.users[target]!!.account, amount)
                "${User.users[target]!!.account.nameCardOrNick}的能量值从${energy}修改为${User.users[target]!!.energyValue}"
            } else "无法识别输入的数字"
        } else "该用户的能量值无效"
    } else "无法识别该用户"
}

fun queryUserEnergy(target: Long?): String {
    return if (target != null) "${User.users[target]!!.account.nameCardOrNick}的能量值是${User.users[target]!!.energyValue}"
    else "无法识别该用户"
}