package org.ritsu.mirai.plugin.kernel

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import net.mamoe.mirai.console.util.safeCast
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import org.ritsu.mirai.plugin.entity.AnonymousMessage
import org.ritsu.mirai.plugin.entity.Grp
import org.ritsu.mirai.plugin.entity.User
import java.io.File
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 此函数的作用是在bot开机时将本地保存的json数据加载进bot中自定义的类中，此函数在启动函数中调用
 *
 * @author RyouonRitsu
 * @param groups 当前bot的群列表
 */
fun loadData(groups: ContactList<Group>) {
    //加载所有群成员到自定义User类中
    groups.forEach { group ->
        group.members.forEach {
            User.getUser(it)
        }
    }
    //读取json文件
    val file = File("./data/UsersData.json")
    val jsonString = file.readText()
    //转为JSONArray对象
    val jsonArr = JSON.parseArray(jsonString) ?: return
    //遍历JSONArray
    for (it in jsonArr) {
        val jsonObj = it as JSONObject
        val user = User.users[jsonObj.getLongValue("id")]
        user?.let { load(jsonObj, it, listOf("account")) }
    }
}

/**
 * 此函数的作用是在bot开机时将本地保存的有关匿名消息的json数据加载进bot中自定义的类中，此函数在启动函数中调用
 *
 * @author ljm
 */
fun loadMessage() {
    val file = File("./data/AnonymousMessage.json")
    val jsonString = file.readText()
    val jsonArr = JSON.parseArray(jsonString) ?: return
    //遍历JSONArray
    for (it in jsonArr) {
        val jsonObj = it as JSONObject
        AnonymousMessage.put(jsonObj.getIntValue("id"))
        AnonymousMessage.arrOccupied.add(jsonObj.getIntValue("id"))
        val messageInfo = AnonymousMessage.messages[jsonObj.getIntValue("id")]
        messageInfo?.let { load(jsonObj, it, listOf("num")) }
    }
    for (i in 1..10) {
        if (AnonymousMessage.arrOccupied.indexOf(i) == -1) AnonymousMessage.arrFree.add(i)
    }
}

/**
 * 此函数的作用是在bot开机时将本地保存的有关群聊成员管理信息的json数据加载进bot中自定义的类中，此函数在启动函数中调用
 *
 * @author ljm
 * @param groups 当前bot的群列表
 */
fun loadGrp(groups: ContactList<Group>) {
    groups.forEach {
        Grp.getGroup(it)
    }
    val file = File("./data/GroupData.json")
    val jsonString = file.readText()
    val jsonArr = JSON.parseArray(jsonString) ?: return
    //遍历JSONArray
    for (it in jsonArr) {
        val jsonObj = it as JSONObject
        val grp = Grp.groups[jsonObj.getLongValue("id")]
        grp?.let { load(jsonObj, it, listOf("group", "mutelist")) }
        val list = jsonObj.getString("mutelist").split(",")
        for (item in list) {
            grp?.mutelist?.add(item.toLong())
        }

    }
}

/**
 * 通过反射加载保存的json文件中的数据到自定义类中，对待需要加载的特殊类型数据如List、Map等需要在每次新增属性的时候进行特殊处理
 *
 * @author RyouonRitsu
 * @param jsonObj json对象
 * @param instance 自定义类的实例
 * @param exclude 在加载时不加载的属性列表
 */
private fun load(jsonObj: JSONObject, instance: Any, exclude: List<String>) {
    instance::class.declaredMemberProperties.filter { it.name !in exclude }.forEach { property ->
        property.isAccessible = true
        when (property.name) {
            //特殊处理列表, Map等需要特殊存储的属性
            else -> if (jsonObj.containsKey(property.name)) {
                when (property.returnType.classifier) {
                    String::class -> property.safeCast<KMutableProperty1<out User, *>>()?.setter?.call(
                        instance,
                        jsonObj.getString(property.name)
                    )
                    Double::class -> property.safeCast<KMutableProperty1<out User, *>>()?.setter?.call(
                        instance,
                        jsonObj.getDoubleValue(property.name)
                    )
                    Int::class -> property.safeCast<KMutableProperty1<out User, *>>()?.setter?.call(
                        instance,
                        jsonObj.getIntValue(property.name)
                    )
                    Long::class -> property.safeCast<KMutableProperty1<out User, *>>()?.setter?.call(
                        instance,
                        jsonObj.getLongValue(property.name)
                    )
                    Boolean::class -> property.safeCast<KMutableProperty1<out User, *>>()?.setter?.call(
                        instance,
                        jsonObj.getBooleanValue(property.name)
                    )
                }
            }
        }
    }
}