package org.ritsu.mirai.plugin.commands

import net.mamoe.mirai.contact.Member
import org.ritsu.mirai.plugin.entity.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.nextDown

/**
 * 获取一天的幸运值
 *
 * @author RyouonRitsu
 * @param sender 发送者
 * @return bot回复
 */
fun luckyValue(sender: Member, tag: Boolean? = false): String {
    val user = User.getUser(sender)
    //标识是否需要更新json数据
    var flag = false
    val format = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    if (user.luckyValueAcquisitionDate != format.format(LocalDate.now())) {
        //如果还没抽过卡就抽卡
        val k = if (tag == true) 10000 else 1
        user.luckyValue = Math.random() / 1.0.nextDown() * k
        user.luckyValueAcquisitionDate = format.format(LocalDate.now())
        flag = true
    }

    //需要更新json数据
    if (flag) user.save()

    val luckyValue = String.format("%.2f", user.luckyValue * 100)
    return "今天的幸运指数是$luckyValue%!"
}