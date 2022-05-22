package org.ritsu.mirai.plugin.kernel

import net.mamoe.mirai.contact.User

fun addEnergy(sender: User, energy: Int) {
    val user = org.ritsu.mirai.plugin.entity.User.getUser(sender)
    user.energyValue += energy
    user.save()
}

fun addEnergy(user: org.ritsu.mirai.plugin.entity.User, energy: Int) {
    user.energyValue += energy
    user.save()
}