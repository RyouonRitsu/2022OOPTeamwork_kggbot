package org.ritsu.mirai.plugin.commands

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.contact.Member
import org.ritsu.mirai.plugin.entity.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.nextDown

fun luckyValue(sender: Member): String {
    val user = User.getUser(sender)
    //读取json文件
    val file = File("./data/UsersData.json")
    var jsonString = file.readText()
    //转为JSONArray对象
    var jsonArr = JSONArray.parseArray(jsonString)
    var jsonObject: JSONObject? = null
    var flag = false
    //如果JSONArray为空
    if (jsonArr == null) {
        //直接新建数据
        user.luckyValue = Math.random() / 1.0.nextDown()
        user.luckyValueAcquisitionDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
    } else {
        //否则查找用户数据
        val iterator = jsonArr.iterator()
        //遍历查找用户数据是否存在
        while (iterator.hasNext()) {
            val jsonObj = iterator.next() as JSONObject
            //如果用户数据存在就读取数据
            if (jsonObj.getLongValue("id") == user.account.id) {
                if (jsonObj.containsKey("luckyValueAcquisitionDate")) user.luckyValueAcquisitionDate =
                    jsonObj.getString("luckyValueAcquisitionDate")
                else user.luckyValueAcquisitionDate = ""
                if (user.luckyValueAcquisitionDate != SimpleDateFormat("yyyy/MM/dd").format(Date())) {
                    //如果还没抽过卡就抽卡
                    user.luckyValue = Math.random() / 1.0.nextDown()
                    user.luckyValueAcquisitionDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
                } else {
                    //如果已经抽过了就继续使用原来的数据
                    user.luckyValue = jsonObj.getDoubleValue("luckyValue")
                }
                flag = true
                jsonObject = jsonObj
                break
            }
        }
        //不存在就新建数据
        if (!flag) {
            user.luckyValue = Math.random() / 1.0.nextDown()
            user.luckyValueAcquisitionDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
        }
    }
    //已经得到数据，写入JSONObject
    if (flag) {
        //存在JSONObject
        jsonObject!!["luckyValue"] = user.luckyValue
        jsonObject["luckyValueAcquisitionDate"] = user.luckyValueAcquisitionDate
    } else {
        //不存在JSONObject
        jsonObject = JSONObject()
        jsonObject["id"] = user.account.id
        jsonObject["luckyValue"] = user.luckyValue
        jsonObject["luckyValueAcquisitionDate"] = user.luckyValueAcquisitionDate
        //JSONObject添加到JSONArray中
        if (jsonArr == null) jsonArr = JSONArray()
        jsonArr.add(jsonObject)
    }
    //JSONArray转为JSONString
    jsonString = jsonArr.toJSONString()
    //写入文件
    file.writeText(jsonString)
    val luckyValue = String.format("%.2f", user.luckyValue * 100)
    return "今天的幸运指数是$luckyValue%!"
}