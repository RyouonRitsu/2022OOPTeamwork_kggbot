package org.ritsu.mirai.plugin.entity

import net.mamoe.mirai.contact.nameCardOrNick
import org.ritsu.mirai.plugin.kernel.addEnergy

/**
 * 管理员单例
 *
 * @author RyouonRitsu
 * @since 0.1.0
 */
object Administrator {
    /**
     * 管理员列表
     */
    val administrators: List<Long> = listOf(
        1L,
    )
    val botList: List<Long> = listOf(
        1L,
    )

    /**
     * bot黑名单列表
     */
    val blacklist = ArrayList<Long>()
}

/**
 * 调整用户能量值
 *
 * @author RyouonRitsu
 * @param target 目标用户id
 * @param amount 变化量，可为负数
 * @return bot回复
 */
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

/**
 * 查询用户能量值
 *
 * @author RyouonRitsu
 * @param target 目标用户id
 * @return bot回复
 */
fun queryUserEnergy(target: Long?): String {
    return if (target != null) "${User.users[target]!!.account.nameCardOrNick}的能量值是${User.users[target]!!.energyValue}"
    else "无法识别该用户"
}