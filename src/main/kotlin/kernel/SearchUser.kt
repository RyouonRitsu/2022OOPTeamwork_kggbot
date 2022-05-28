package org.ritsu.mirai.plugin.kernel

import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain

fun searchFirstUserByAt(message: MessageChain): Long? {
    message.forEach { if (it is At) return it.target }
    return null
}

fun searchAllUsersByAt(message: MessageChain): List<Long> {
    val list = ArrayList<Long>()
    message.forEach { if (it is At) list.add(it.target) }
    return list
}