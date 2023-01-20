package org.ritsu.mirai.plugin.entity

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 自定义的Grp类，用于存放群聊的群名片格式和禁言名单
 *
 * @author ljm
 * @property format 群名片格式的正则表达式的字符串形式
 * @property mutelist 群名片不合规的禁言名单
 * @constructor 创建一个Grp类
 */
class Grp(val group: net.mamoe.mirai.contact.Group) {
    companion object {
        /**
         * 所有匿名消息的HashMap
         */
        val groups = HashMap<Long, Grp>()

        /**
         * 获取群聊，若群聊不存在，则创建一个新的群聊
         */
        fun getGroup(group: net.mamoe.mirai.contact.Group): Grp {
            return groups.getOrPut(group.id) { Grp(group) }
        }
    }

    var format: String? = null
    var mutelist = mutableListOf<Long>()

    /**
     * 保存群成员管理的相关数据到文件
     *
     * @author ljm
     */
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

    //private fun writeJSON(jsonObject: JSONObject) {
    //    jsonObject["format"] = this.format
    //    jsonObject["mutelist"] = this.mutelist
    //}

    /**
     * 通过反射自动写入基础数据类型进JSONObject，对于List、Map等复杂数据类型需要特别指定写入方式
     *
     * @author ljm
     * @param jsonObject 要写入的JSONObject文件
     */
    private fun writeJSON(jsonObject: JSONObject) {
        //使用反射获取所有属性并写入jsonObject
        this::class.declaredMemberProperties.filter { it.name != "group" }.forEach { property ->
            property.isAccessible = true
            when (property.name) {
                //特殊处理列表, Map等需要特殊存储的属性
                "mutelist" -> jsonObject["mutelist"] = this.mutelist.joinToString(",")
                else -> jsonObject[property.name] = property.getter.call(this)
            }
        }
    }
}
