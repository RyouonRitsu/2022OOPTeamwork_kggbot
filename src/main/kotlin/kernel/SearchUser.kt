package org.ritsu.mirai.plugin.kernel

import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain

/**
 * 通过At寻找第一个被发现的成员的id
 *
 * @author RyouonRitsu
 * @param message 待查找的消息链，通常是一个监听中的message属性
 * @return 被发现的成员的id，如果没有被发现，则返回null
 */
fun searchFirstUserByAt(message: MessageChain): Long? {
    message.forEach { if (it is At) return it.target }
    return null
}

/**
 * 通过At寻找所有被发现的成员的id
 *
 * @author RyouonRitsu
 * @param message 待查找的消息链，通常是一个监听中的message属性
 * @return 被发现的成员的id列表，如果没有被发现，则返回空列表
 */
fun searchAllUsersByAt(message: MessageChain): List<Long> {
    val list = ArrayList<Long>()
    message.forEach { if (it is At) list.add(it.target) }
    return list
}