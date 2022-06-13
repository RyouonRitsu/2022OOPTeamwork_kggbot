package org.ritsu.mirai.plugin.entity

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 自定义的User类，用于存放附加的用户数据，具有存储功能
 *
 * @author RyouonRitsu
 * @since 0.1.0
 * @property account 用户实际账号，类型是mirai的User类型，使用这个属性并不能向下转型为Member或Friend，只能提供基础的User信息，需要使用Member或Friend的属性请在直接使用，而不是用这个属性
 * @constructor 创建一个User类
 */
class User(val account: net.mamoe.mirai.contact.User) {
    companion object {
        /**
         * 所有用户的HashMap
         */
        val users = HashMap<Long, User>()

        /**
         * 用户的对话锁
         */
        val conversationLock = HashMap<Long, Boolean>()
        val weatherLock = HashMap<Long, Boolean>()

        /**
         * 获取用户，若用户不存在，则创建一个新的用户
         */
        fun getUser(account: net.mamoe.mirai.contact.User): User {
            return users.getOrPut(account.id) { User(account) }
        }
    }

    var luckyValue: Double = -1.0
    var luckyValueAcquisitionDate: String = ""
    var signedCount: Int = 0
    var signedDate: String = ""
    var energyValue: Int = 0
    var blockAnonymousMessage: Boolean = false

    /**
     * 保存用户数据到文件，此方法在新增用户属性的时候不需要修改
     *
     * @author RyouonRitsu
     */
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

    /**
     * 通过反射自动写入基础数据类型进JSONObject，对于List、Map等复杂数据类型需要特别指定写入方式
     *
     * @author RyouonRitsu
     * @param jsonObject 要写入的JSONObject文件
     */
    private fun writeJSON(jsonObject: JSONObject) {
        //使用反射获取所有属性并写入jsonObject
        this::class.declaredMemberProperties.filter { it.name != "account" }.forEach { property ->
            property.isAccessible = true
            when (property.name) {
                //特殊处理列表, Map等需要特殊存储的属性
                else -> jsonObject[property.name] = property.getter.call(this)
            }
        }
    }
}
