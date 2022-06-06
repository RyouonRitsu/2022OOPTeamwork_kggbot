package org.ritsu.mirai.plugin.entity

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import java.io.File

/**
 * @author 卢嘉美-20373814
 * @version jdk15.0.2
 */
class Grp (val group: net.mamoe.mirai.contact.Group) {
    companion object {
        val groups = HashMap<Long, Grp>()
        fun getGroup(group: net.mamoe.mirai.contact.Group): Grp {
            return groups.getOrPut(group.id) { Grp(group) }
        }
    }

    var format: Regex? = null
    var mutelist = mutableListOf<Long>()

    fun save() {
        //读取json文件
        val file = File("./data/GroupData.json")
        var jsonString = file.readText()
        //转为JSONArray对象
        var jsonArr = JSON.parseArray(jsonString)
        var jsonObject: JSONObject? = null
        //如果JSONArray不为空
        if (jsonArr != null) {
            for (it in jsonArr) {
                val jsonObj = it as JSONObject
                if (jsonObj.getLongValue("id") == this.group.id) {
                    jsonObject = jsonObj
                    break
                }
            }
        }
        if (jsonObject == null) {
            //不存在JSONObject
            jsonObject = JSONObject()
            jsonObject["id"] = this.group.id
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
        jsonObject["format"] = this.format
        jsonObject["mutelist"] = this.mutelist
    }
}
