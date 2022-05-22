package org.ritsu.mirai.plugin.kernel

import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain

fun searchUserByAt(message: MessageChain): Long? {
    message.forEach { if (it is At) return it.target }
    return null
}