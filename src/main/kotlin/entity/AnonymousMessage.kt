package org.ritsu.mirai.plugin.entity

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.PlainText
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 自定义的AnonymousMessage类，用于存放匿名消息的发送方和接收方id
 *
 * @author ljm
 * @property num 匿名消息的编号
 * @property senderid 匿名消息发送者id
 * @property receiverid 匿名消息接收者id
 * @constructor 创建一个AnonymousMessage类
 */
class AnonymousMessage(val num: Int) {
    companion object {
        /**
         * 所有匿名消息的HashMap
         */
        val messages = HashMap<Int, AnonymousMessage>()

        /**
         * 当前空闲编号列表
         */
        val arrFree = mutableListOf<Int>()

        /**
         * 当前已被占用列表编号
         */
        val arrOccupied = mutableListOf<Int>()

        /**
         * 获取对应编号的匿名消息对象
         */
        fun getAnonymousMessage(num: Int): AnonymousMessage? {
            return messages[num]
        }

        /**
         * 放入一个匿名消息对象进入匿名消息的HashMap中
         */
        fun put(num: Int) {
            messages.put(num, AnonymousMessage(num))
        }

        /**
         * 创建一个匿名消息时为其分配编号
         */
        suspend fun getRandom(bot: Bot): Int {
            val a: Int
            if (arrFree.isNotEmpty()) {
                a = arrFree.first()
                arrFree.removeFirst()
                arrOccupied.add(a)
            } else {
                a = arrOccupied.first()
                if (getAnonymousMessage(a) != null) {
                    val sender = getAnonymousMessage(a)?.senderid
                    sender?.let { bot.getFriend(it)?.sendMessage(PlainText("对不起，由于长时间没有响应，您的编号为" + a + "的消息将被丢弃")) }
                    val receicer = getAnonymousMessage(a)?.receiverid
                    receicer?.let { bot.getFriend(it)?.sendMessage(PlainText("对不起，由于长时间没有响应，您的编号为" + a + "的消息将被丢弃")) }
                    messages.remove(a)
                }
                arrOccupied.removeFirst()
                arrOccupied.add(a)
                getAnonymousMessage(a)?.delete()
            }
            return a
        }

        /**
         * 匿名消息被回复后释放对应编号
         */
        fun release(num: Int, receiverID: Long): AnonymousMessage? {
            val index = arrOccupied.indexOf(num)
            val obj = getAnonymousMessage(num)
            if (obj != null && index != -1) {
                if (obj.receiverid == receiverID) {
                    arrOccupied.removeAt(index)
                    arrFree.add(num)
                    messages.remove(num)
                    return obj
                }
            }
            return null
        }

        /**
         * 一个用户拒绝接收匿名消息后回复之前所有该用户为接收者且还未被回复的匿名消息，并设置该用户以后不再接收匿名消息
         */
        suspend fun refuse(num: Long, bot: Bot) {
            val iterator = messages.entries.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.value.receiverid == num) {
                    val number = item.value.num
                    arrOccupied.remove(number)
                    arrFree.add(number)
                    item.value.delete()
                    iterator.remove()
                    val sender = bot.getFriend(item.value.senderid)
                    sender?.sendMessage(PlainText("抱歉....对方拒绝接收匿名消息"))
                }
            }
        }
    }

    var senderid: Long = 0
    var receiverid: Long = 0

    /**
     * 保存匿名消息的相关数据到文件
     *
     * @author ljm
     */
    fun save() {
        //读取json文件
        val file = File("./data/AnonymousMessage.json")
        var jsonString = file.readText()
        //转为JSONArray对象
        var jsonArr = JSON.parseArray(jsonString)
        var jsonObject: JSONObject? = null
        //如果JSONArray不为空
        if (jsonArr != null) {
            for (it in jsonArr) {
                val jsonObj = it as JSONObject
                if (jsonObj.getValue("id") == this.num) {
                    jsonObject = jsonObj
                    break
                }
            }
        }
        if (jsonObject == null) {
            //不存在JSONObject
            jsonObject = JSONObject()
            jsonObject["id"] = this.num
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
     * 删除文件中的匿名消息相关信息
     *
     * @author ljm
     */
    fun delete() {
        //读取json文件
        val file = File("./data/AnonymousMessage.json")
        var jsonString = file.readText()
        //转为JSONArray对象
        val jsonArr = JSON.parseArray(jsonString)
        //如果JSONArray不为空
        if (jsonArr != null) {
            for (it in jsonArr) {
                val jsonObj = it as JSONObject
                if (jsonObj.getValue("id") == this.num) {
                    jsonArr.remove(jsonObj)
                    break
                }
            }
        }
        //JSONArray转为JSONString
        jsonString = jsonArr.toJSONString()
        //写入文件
        file.writeText(jsonString)
    }

    /**
     * 通过反射自动写入基础数据类型进JSONObject，对于List、Map等复杂数据类型需要特别指定写入方式
     *
     * @author ljm
     * @param jsonObject 要写入的JSONObject文件
     */
    private fun writeJSON(jsonObject: JSONObject) {
        //使用反射获取所有属性并写入jsonObject
        this::class.declaredMemberProperties.filter { it.name != "num" }.forEach { property ->
            property.isAccessible = true
            when (property.name) {
                //特殊处理列表, Map等需要特殊存储的属性
                else -> jsonObject[property.name] = property.getter.call(this)
            }
        }
    }
}