package org.ritsu.mirai.plugin.kernel

import net.mamoe.mirai.contact.User

/**
 * 调整群成员的能量值，通过加和的方式在原有基础上进行增减，并自动保存到数据文件中
 *
 * @author RyouonRitsu
 * @param sender 要调整的群成员
 * @param energy 要调整的能量值，可为负数，负数表示在原基础上减少能量值
 */
fun addEnergy(sender: User, energy: Int) {
    val user = org.ritsu.mirai.plugin.entity.User.getUser(sender)
    user.energyValue += energy
    user.save()
}

/**
 * 调整成员的能量值，通过加和的方式在原有基础上进行增减，并自动保存到数据文件中
 *
 * @author RyouonRitsu
 * @param user 要调整的成员，这个成员必须是自定义的User类的实例
 * @param energy 要调整的能量值，可为负数，负数表示在原基础上减少能量值
 */
fun addEnergy(user: org.ritsu.mirai.plugin.entity.User, energy: Int) {
    user.energyValue += energy
    user.save()
}