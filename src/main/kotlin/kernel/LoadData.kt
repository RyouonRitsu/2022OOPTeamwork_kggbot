package org.ritsu.mirai.plugin.kernel

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import net.mamoe.mirai.console.util.safeCast
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import org.ritsu.mirai.plugin.entity.User
import java.io.File
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun loadData(groups: ContactList<Group>) {
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
                }
            }
        }
    }
}