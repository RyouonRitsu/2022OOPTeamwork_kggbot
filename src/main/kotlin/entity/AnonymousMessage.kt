package org.ritsu.mirai.plugin.entity

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.PlainText
import java.io.File

/**
 * @author 卢嘉美-20373814
 * @version jdk15.0.2
 */
class AnonymousMessage(val num: Int) {
    companion object {
        val messages = HashMap<Int, AnonymousMessage>()
        val arrFree = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val arrOccupied = mutableListOf<Int>()
        fun getAnonymousMessage(num: Int): AnonymousMessage? {
            return messages[num]
        }

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

    private fun writeJSON(jsonObject: JSONObject) {
        jsonObject["senderid"] = this.senderid
        jsonObject["receiverid"] = this.receiverid
    }
}