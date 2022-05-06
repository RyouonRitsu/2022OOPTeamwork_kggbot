package org.ritsu.mirai.plugin.kernel

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import org.ritsu.mirai.plugin.entity.User
import java.io.File

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
    val jsonArr = JSONArray.parseArray(jsonString) ?: return
    //遍历JSONArray
    for (it in jsonArr) {
        val jsonObj = it as JSONObject
        val user = User.users[jsonObj.getLongValue("id")]
        user?.let {
            if (jsonObj.containsKey("luckyValue")) it.luckyValue = jsonObj.getDoubleValue("luckyValue")
            if (jsonObj.containsKey("luckyValueAcquisitionDate")) it.luckyValueAcquisitionDate = jsonObj.getString("luckyValueAcquisitionDate")
            if (jsonObj.containsKey("signedCount")) it.signedCount = jsonObj.getIntValue("signedCount")
            if (jsonObj.containsKey("signedDate")) it.signedDate = jsonObj.getString("signedDate")
            if (jsonObj.containsKey("energyValue")) it.energyValue = jsonObj.getIntValue("energyValue")
        }
    }
}