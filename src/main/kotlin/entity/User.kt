package org.ritsu.mirai.plugin.entity

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import java.io.File

class User(val account: net.mamoe.mirai.contact.User) {
    companion object {
        val users = HashMap<Long, User>()

        fun getUser(account: net.mamoe.mirai.contact.User): User {
            return users.getOrPut(account.id) { User(account) }
        }
    }

    var luckyValue: Double = -1.0
    var luckyValueAcquisitionDate: String = ""
    var signedCount: Int = 0
    var signedDate: String = ""
    var energyValue: Int = 0

    fun save() {
        //读取json文件
        val file = File("./data/UsersData.json")
        var jsonString = file.readText()
        //转为JSONArray对象
        var jsonArr = JSON.parseArray(jsonString)
        var jsonObject: JSONObject? = null
        //如果JSONArray不为空
        if (jsonArr != null) {
            for (it in jsonArr) {
                val jsonObj = it as JSONObject
                if (jsonObj.getLongValue("id") == this.account.id) {
                    jsonObject = jsonObj
                    break
                }
            }
        }
        if (jsonObject == null) {
            //不存在JSONObject
            jsonObject = JSONObject()
            jsonObject["id"] = this.account.id
            writeJSON(jsonObject)
            //JSONObject添加到JSONArray中
            if (jsonArr == null) jsonArr = JSONArray()
            jsonArr.add(jsonObject)
        } else {
            //存在JSONObject
            writeJSON(jsonObject)
        }
        //JSONArray转为JSONString
        jsonString = jsonArr.toJSONString()
        //写入文件
        file.writeText(jsonString)
    }

    private fun writeJSON(jsonObject: JSONObject) {
        jsonObject["luckyValue"] = this.luckyValue
        jsonObject["luckyValueAcquisitionDate"] = this.luckyValueAcquisitionDate
        jsonObject["signedCount"] = this.signedCount
        jsonObject["signedDate"] = this.signedDate
        jsonObject["energyValue"] = this.energyValue
    }
}
