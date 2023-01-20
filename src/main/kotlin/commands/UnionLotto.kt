package org.ritsu.mirai.plugin.commands

import kotlin.math.ceil

fun unionLotto(str: String): String {
    val balls = str.split(" ")
    if (balls.size != 7) return "双色球要有6个红球和1个蓝球哦，每个数之间请用一个空格隔开\n"
    val nums = mutableListOf<Int>()
    try {
        balls.forEach { s -> nums.add(s.toInt()) }
    } catch (_: java.lang.NumberFormatException) {
        return "前六个数代表红球，最后一个数代表蓝球。红球可以取1~33，蓝球可以取1~16，每个数之间请用一个空格隔开\n"
    }
    if (nums[6] !in 1..16) return "前六个数代表红球，最后一个数代表蓝球。红球可以取1~33，蓝球可以取1~16，每个数之间请用一个空格隔开\n"
    val set = HashSet<Int>()
    var flag = false
    for (i in 0..5) {
        if (nums[i] !in 1..33) return "前六个数代表红球，最后一个数代表蓝球。红球可以取1~33，蓝球可以取1~16，每个数之间请用一个空格隔开\n"
        if (!set.add(nums[i])) flag = true
    }
    if (flag) return "红球的数字不能重复哦\n"
    val red = mutableListOf<Int>()
    while (red.size < 6) {
        val t = ceil(Math.random() * 33).toInt()
        if (t !in red) red.add(t)
    }
    red.sort()
    var cnt = 0
    for (i in nums) {
        if (i in red) cnt++
    }
    val blue = ceil(Math.random() * 16).toInt()
    var result = if (cnt <= 2 && nums[6] == blue) "恭喜你中了六等奖5元！\n"
    else if (cnt == 4 && nums[6] != blue || cnt == 3 && nums[6] == blue) "恭喜你中了五等奖10元！\n"
    else if (cnt == 5 && nums[6] != blue || cnt == 4 && nums[6] == blue) "恭喜你中了四等奖200元！\n"
    else if (cnt == 5 && nums[6] == blue) "恭喜你中了三等奖3000元！！\n"
    else if (cnt == 6 && nums[6] != blue) "恭喜你中了二等奖500万元！！！\n"
    else if (cnt == 6 && nums[6] == blue) "恭喜你中了一等奖500万元！！！\n"
    else "抱歉，这次没中奖ToT\n"
    result += "开奖结果：\n红球："
    red.forEach { s -> result += "$s " }
    return "$result\n蓝球：$blue\n"
}