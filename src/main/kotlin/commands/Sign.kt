package org.ritsu.mirai.plugin.commands

import org.ritsu.mirai.plugin.entity.User
import java.text.SimpleDateFormat
import java.util.*

fun sign(sender: net.mamoe.mirai.contact.User, percent: Double): String {
    val user = User.getUser(sender)
    //标识是否需要更新json数据
    var flag = false
    var acquisitionValue = 0

    //如果还没签到就签到
    if (user.signedDate != SimpleDateFormat("yyyy/MM/dd").format(Date())) {
        //从未签过到
        if (user.signedDate == "") user.signedCount = 1
        else {
            //以前签过到
            val c1 = Calendar.getInstance()
            c1.time = SimpleDateFormat("yyyy/MM/dd").parse(user.signedDate)
            val c2 = Calendar.getInstance()
            c2.time = SimpleDateFormat("yyyy/MM/dd").parse(SimpleDateFormat("yyyy/MM/dd").format(Date()))
            c2.add(Calendar.DATE, -1)
            if (c1.time.before(c2.time)) {
                //断签了
                user.signedCount = 1
            } else user.signedCount++
        }
        user.signedDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
        acquisitionValue = (4 * percent * (1 + user.signedCount / 10.0)).toInt()
        user.energyValue += acquisitionValue
        flag = true
    }

    //需要更新json数据
    if (flag) user.save()

    return if (flag) "签到成功!\n连续签到天数: ${user.signedCount}\n今日获得能量值: $acquisitionValue\n当前能量值是: ${user.energyValue}"
    else ""
}