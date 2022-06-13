package org.ritsu.mirai.plugin.commands

import org.ritsu.mirai.plugin.entity.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 签到功能，每天首次签到可以获得能量值
 *
 * @author RyouonRitsu
 * @param sender 发起者
 * @param percent 能量值获得倍率
 * @return bot回复
 */
fun sign(sender: net.mamoe.mirai.contact.User, percent: Double): String {
    val user = User.getUser(sender)
    //标识是否需要更新json数据
    var flag = false
    var acquisitionValue = 0
    val format = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    //如果还没签到就签到
    if (user.signedDate != format.format(LocalDate.now())) {
        //从未签过到
        if (user.signedDate == "") user.signedCount = 1
        else {
            //以前签过到
            val d1 = LocalDate.parse(user.signedDate, format)
            val d2 = LocalDate.now().minusDays(1)
            if (d1.isBefore(d2)) {
                //断签了
                user.signedCount = 1
            } else user.signedCount++
        }
        user.signedDate = format.format(LocalDate.now())
        acquisitionValue = (4 * percent * (1 + user.signedCount / 10.0)).toInt()
        user.energyValue += acquisitionValue
        flag = true
    }

    //需要更新json数据
    if (flag) user.save()

    return if (flag) "签到成功!\n连续签到天数: ${user.signedCount}\n今日获得能量值: $acquisitionValue\n当前能量值是: ${user.energyValue}"
    else ""
}