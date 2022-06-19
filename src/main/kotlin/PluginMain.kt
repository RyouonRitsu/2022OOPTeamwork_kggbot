package org.ritsu.mirai.plugin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.selectMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import org.ritsu.mirai.plugin.commands.*
import org.ritsu.mirai.plugin.commands.translate.NotAvailable
import org.ritsu.mirai.plugin.commands.translate.languageType
import org.ritsu.mirai.plugin.commands.translate.translate
import org.ritsu.mirai.plugin.commands.wordcloud.getGroupWordCloud
import org.ritsu.mirai.plugin.entity.*
import org.ritsu.mirai.plugin.kernel.addEnergy
import org.ritsu.mirai.plugin.kernel.searchFirstUserByAt
import org.ritsu.mirai.plugin.kernel.textToPicture
import java.awt.Font
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `org.example.mirai.plugin.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.OOP.mirai",
        name = "kggbot",
        version = "0.1.0"
    ) {
        author("RyouonRitsu")
        info(
            """
            这是一个kggbot插件, 
            在这里描述插件的功能和用法等.
        """.trimIndent()
        )
        // author 和 info 可以删除.
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"
        val eventChannel = GlobalEventChannel.parentScope(this)
        //监听新加群消息并欢迎
        eventChannel.subscribeAlways<MemberJoinEvent> {
            group.sendMessage(
                PlainText("欢迎") + At(member).followedBy(
                    PlainText(
                        "加入${group.name}! 请先仔细阅读群内公告及注意事项, " +
                            "如果可以的话也请修改一下群名片, 之后就可以和大家一起愉快的玩耍啦! 有任何需要的话都可以随时找我! 不知道该干什么的话就说\"kgghelp\"!"
                    )
                )
            )
        }
        eventChannel.subscribeAlways<GroupMessageEvent> {
            //词云功能存储聊天记录
            val groupMessagesRecord = File("./data/${group.id}")
            //如果文件上次修改时间不为今天，则覆盖写入
            if (!groupMessagesRecord.exists() || !Instant.ofEpochMilli(groupMessagesRecord.lastModified())
                    .atZone(ZoneId.of("Asia/Shanghai")).toLocalDate().isEqual(LocalDate.now())
            ) groupMessagesRecord.writeText(
                "${message.filterIsInstance<PlainText>().joinToString("，")}，",
                Charsets.UTF_8
            )
            else groupMessagesRecord.appendText(
                "${message.filterIsInstance<PlainText>().joinToString("，")}，",
                Charsets.UTF_8
            )
            //黑名单和对话锁
            if (sender.id in Administrator.blacklist || User.conversationLock[sender.id] == true) return@subscribeAlways
            if (User.weatherLock[sender.id] == true) {
                User.weatherLock[sender.id] = false
                val location = message.contentToString()
                if ("lon=" in location && "lat=" in location) {
                    val lon = location.indexOf("lon=") + 4
                    val lat = location.indexOf("lat=") + 4
                    val loc = location.substring(lon, lon + 6) + "," + location.substring(lat, lat + 5)
                    group.sendMessage(message.quote() + getNowWeather(loc))
                } else if ("lng\":" in location && "lat\":" in location) {
                    val lon = location.indexOf("lng\":") + 6
                    val lat = location.indexOf("lat\":") + 6
                    val loc = location.substring(lon, lon + 6) + "," + location.substring(lat, lat + 5)
                    group.sendMessage(message.quote() + getNowWeather(loc))
                } else group.sendMessage(message.quote() + "这不是一个定位，请重试！\n")
                return@subscribeAlways
            }
            //群消息
            //管理员命令
            if (sender.id in Administrator.administrators && message.contentToString().startsWith("**")) {
                if ("能量值" in message.contentToString()) {
                    group.sendMessage(
                        adjustUserEnergy(
                            searchFirstUserByAt(message),
                            message.contentToString().replaceBefore("能量值", "").replace("能量值", "").toIntOrNull()
                        )
                    )
                } else if ("查询" in message.contentToString()) {
                    group.sendMessage(queryUserEnergy(searchFirstUserByAt(message)))
                } else if ("小黑屋" in message.content) {
                    val userId = searchFirstUserByAt(message)
                    if (userId != null && userId !in Administrator.blacklist) {
                        Administrator.blacklist.add(userId)
                        group.sendMessage("操作成功!")
                    } else if (userId != null) group.sendMessage("操作失败! 该用户已在小黑屋里!")
                    else group.sendMessage("操作失败! 请检查命令是否正确!")
                } else if ("解除" in message.content) {
                    val userId = searchFirstUserByAt(message)
                    if (userId != null && userId in Administrator.blacklist) {
                        Administrator.blacklist.remove(userId)
                        group.sendMessage("操作成功!")
                    } else if (userId != null) group.sendMessage("操作失败! 该用户不在小黑屋里!")
                    else group.sendMessage("操作失败! 请检查命令是否正确!")
                }
            }
            //普通命令
            if ((sender.permission == MemberPermission.OWNER || sender.permission == MemberPermission.ADMINISTRATOR)
                && message.contentToString().startsWith("!!!")
            ) {
                atMember(message, group)
            }
            if ((sender.permission == MemberPermission.OWNER || sender.permission == MemberPermission.ADMINISTRATOR)
                && message.contentToString().startsWith("禁言群成员")
            ) {
                mute(message, group)
            }
            if ((sender.permission == MemberPermission.OWNER || sender.permission == MemberPermission.ADMINISTRATOR)
                && message.contentToString().startsWith("清理群成员")
            ) {
                kick(group, message)
            }
            //复读示例
            else if (message.contentToString().startsWith("复读")) {
                group.sendMessage(message.contentToString().replace("复读", ""))
            }
            //成语接龙模式
            else if (IdiomSolitaire.gameMap[group.id] == true) {
                if (message.content == "不玩了") {
                    group.sendMessage("游戏已结束!")
                    IdiomSolitaire.gameMap[group.id] = false
                } else if (message.content.length == 4) {
                    val (reply, key) = idiomSolitaire(message.content, IdiomSolitaire.keyMap[group.id]!!.first)
                    if (key != null) {
                        if (message.content in IdiomSolitaire.keyMap[group.id]!!)
                            group.sendMessage("这个重复了哦! 请换一个吧~ 当前接龙的字是\"${getPinYin(IdiomSolitaire.keyMap[group.id]!!.first)}\"!")
                        else {
                            IdiomSolitaire.keyMap[group.id]!!.addFirst(key)
                            val reward = (10..20).random()
                            addEnergy(sender, reward)
                            group.sendMessage(
                                reply + "\n${sender.nameCardOrNick}已获得${reward}点能量值奖励, 当前有${
                                    User.getUser(
                                        sender
                                    ).energyValue
                                }点能量值!"
                            )
                        }
                    } else group.sendMessage(reply)
                } else group.sendMessage("这个不可以哦! 请换一个吧~ 当前接龙的字是\"${getPinYin(IdiomSolitaire.keyMap[group.id]!!.first)}\"!")
            }
            //kgg命令
            else if (
                message.contentToString() == "kgg"
            ) {
                val result: String = sign(sender, 1.0)
                group.sendMessage(Reply.replies[message.contentToString()]!!.random())
                if (result != "") sender.sendMessage(result)
            } else if (message.contentToString().startsWith("kgg")) {
                val cmd = message.contentToString().replaceFirst("kgg", "")
                if (cmd == "抽卡") {
                    //发送消息
                    group.sendMessage(sender.nameCardOrNick + luckyValue(sender))
                    val result = sign(sender, User.getUser(sender).luckyValue + 1)
                    if (result != "") sender.sendMessage(result)
                } else if (cmd == "help") {
                    val inputStream = File("./data/Image/help/help.png").toExternalResource()
                    val id = group.uploadImage(inputStream).imageId
                    withContext(Dispatchers.IO) { inputStream.close() }
                    group.sendMessage(Image(id))
//                    textToPicture(
//                        "群聊功能：\n\n" + Help.toString(Help.funcGroup).trim(),
//                        Font("等线", Font.PLAIN, 50),
//                        File("./data/Image/temp_kgghelp.png"),
//                        File("./data/Image/bg_help.png")
//                    )
//                    val inputStream = File("./data/Image/temp_kgghelp.png").toExternalResource()
//                    val id = group.uploadImage(inputStream).imageId
//                    withContext(Dispatchers.IO) { inputStream.close() }
//                    group.sendMessage(Image(id))
                } else if (cmd.startsWith("h")) {
                    var name = cmd.replace("h", "")
                    if (name == "") name = "0"
                    try {
                        val inputStream = File("./data/Image/help/$name.png").toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        group.sendMessage(Image(id))
                    } catch (_: Exception) {
                        group.sendMessage(message.quote() + "请输入合法的功能序号哦，不知道序号的话请说\"kgghelp\"")
                    }
                } else if (cmd.startsWith("我今天") && "吃什么" in cmd) {
                    var type = cmd.replace("我今天", "")
                        .replaceAfter("吃什么", "").replace("吃什么", "")
                    var n: Int? = 1
                    var temp = cmd.replaceBefore("吃什么", "").replaceFirst("吃什么", "")
                    if ("x" in cmd) {
                        n = cmd.replaceBefore("x", "").replace("x", "").toIntOrNull()
                        temp = temp.replaceAfterLast("x", "").substringBeforeLast("x")
                    }
                    if (type == "" || temp != "") type = temp
                    if (n == 1) group.sendMessage(message.quote() + randomEat(type))
                    else if (n != null && n in 2..10) group.sendMessage(message.quote() + randomEat(type, n))
                    else group.sendMessage(message.quote() + "重复抽取命令格式错误! 请尝试2-10的整数!")
                } else if (cmd == "吃的类型") {
                    group.sendMessage(dishLs())
                } else if (cmd.startsWith("mix")) {
                    val result = emojiMix(cmd.replace("mix", ""))
                    if (result.startsWith("./data/Image/")) {
                        val inputStream = File(result).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        group.sendMessage(Image(id))
                        withContext(Dispatchers.IO) { inputStream.close() }
                    } else group.sendMessage(result)
                } else if (cmd.startsWith("dice")) {
                    val n = cmd.replace("dice", "").toLongOrNull()
                    if (n != null && n > 0) group.sendMessage(message.quote() + "你roll出了${(1..n).random()}")
                    else group.sendMessage(message.quote() + "看不懂你要抽到多少哦, 请尝试大于1的整数!")
                } else if (cmd.startsWith("占卜一下")) {
                    if (cmd.replaceFirst("占卜一下", "") == "") group.sendMessage(message.quote() + "不写内容就来占卜吗?")
                    else group.sendMessage(divination(sender, cmd.replaceFirst("占卜一下", "")))
                } else if (cmd.startsWith("t")) {
                    if (group.id in NotAvailable.groups) group.sendMessage(message.quote() + "此功能在该群不可用!")
                    else if (cmd.length > 300) group.sendMessage(message.quote() + "你要翻译的内容太长啦, 弄少一点再来吧!")
                    else if ("\n" in cmd) group.sendMessage(message.quote() + "你要翻译的内容不能包含换行哦!")
                    else if ("->" in cmd) group.sendMessage(
                        message.quote() +
                            translate(
                                cmd.replaceFirst("t", "").replaceAfterLast("->", "").substringBeforeLast("->"),
                                cmd.substringAfterLast("->").replaceFirst("->", "")
                            )
                    )
                    else group.sendMessage(message.quote() + translate(cmd.replaceFirst("t", "")))
                } else if (cmd == "支持语言") {
                    group.sendMessage(message.quote() + "目前支持的语言有: " + languageType())
                } else if ("搜图" in cmd) {
                    User.conversationLock[sender.id] = true
                    val user = User.getUser(sender)
                    var flag = true
                    var id: String? = null
                    message.filterIsInstance<Image>().forEach {
                        if (user.energyValue >= 120) {
                            val (result, msg) = searchImageSource(it.queryUrl())
                            if (msg == "./data/Image/temp_thumbnail.png") {
                                val inputStream = File(msg).toExternalResource()
                                id = group.uploadImage(inputStream).imageId
                                withContext(Dispatchers.IO) { inputStream.close() }
                            }
                            addEnergy(user, -120)
                            if (id != null) group.sendMessage(message.quote() + Image(id!!) + "\n${result}*你还剩余${user.energyValue}点能量值!")
                            else group.sendMessage(message.quote() + "${result}*你还剩余${user.energyValue}点能量值!")
                        } else group.sendMessage(message.quote() + "你的能量值不足120, 无法搜图!")
                        flag = false
                    }
                    if (flag) {
                        if (user.energyValue >= 120) {
                            group.sendMessage(message.quote() + "请在30秒内发送图片或图片链接!")
                            val imageUrl = selectMessages {
                                has<Image> { it.queryUrl() }
                                has<PlainText> { it.content }
                                default { "default" }
                                timeout(30_000) { "timeout" }
                            }
                            if (imageUrl == "timeout") group.sendMessage(At(sender).followedBy(PlainText("超时了, 或者没有收到你的图片, 请重试!")))
                            else if (imageUrl.startsWith("http")) {
                                val (result, msg) = searchImageSource(imageUrl)
                                if (msg == "./data/Image/temp_thumbnail.png") {
                                    val inputStream = File(msg).toExternalResource()
                                    id = group.uploadImage(inputStream).imageId
                                    withContext(Dispatchers.IO) { inputStream.close() }
                                }
                                addEnergy(user, -120)
                                if (id != null) group.sendMessage(At(sender).followedBy(Image(id!!) + PlainText("\n${result}*你还剩余${user.energyValue}点能量值!")))
                                else group.sendMessage(At(sender).followedBy(PlainText("${result}*你还剩余${user.energyValue}点能量值!")))
                            } else group.sendMessage(At(sender).followedBy(PlainText(if (imageUrl == "default") "请发送图片或图片链接!" else "识别失败, 请重试!")))
                        } else group.sendMessage(message.quote() + "你的能量值不足120, 无法搜图!")
                    }
                } else if (cmd.startsWith("python")) {
                    val (result, error) = runPython(cmd)
                    try {
                        if (result != null && result != "") group.sendMessage(
                            message.quote() + PlainText(
                                result.replace(
                                    Regex(".:\\\\.*\\..."),
                                    "**此为机密领域, 妄图窥探的话是会被关小黑屋的**"
                                )
                            )
                        )
                        else if (result != null) group.sendMessage(message.quote() + PlainText("运行结果为空!"))
                        else group.sendMessage(message.quote() + PlainText("Error: TLE"))
                    } catch (e: Exception) {
                        group.sendMessage(message.quote() + PlainText(e.message ?: "Error: RE"))
                    }
                    if (error != null) group.sendMessage(At(User.users[1L]!!.account).followedBy(PlainText("主人! ${sender.nameCardOrNick}玩弄我!\n${error}")))
                } else if (cmd == "天气") {
                    User.weatherLock[sender.id] = true
                    group.sendMessage(message.quote() + "请发送定位!")
                } else if (cmd.startsWith("metar")) {
                    group.sendMessage(message.quote() + getMetar(cmd.replaceFirst("metar", "")))
                } else if (cmd.startsWith("来点")) {
                    if (sender.id !in Administrator.administrators) group.sendMessage(message.quote() + "对不起，您没有权限使用该功能")
                    else {
                        val id: String
                        val (msg, result) = getRandomPixivPic(cmd.replaceFirst("来点", ""))
                        if (result == "./data/Image/temp_pixiv.jpg" || result == "./data/Image/temp_pixiv.png") {
                            val inputStream = File(result).toExternalResource()
                            id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        } else if (result != null) group.sendMessage(message.quote() + "$msg\n$result")
                        else group.sendMessage(message.quote() + msg)
                    }
                } else if (cmd == "全国油价") {
                    textToPicture(
                        getOil("全国"),
                        Font("等线", Font.PLAIN, 50),
                        File("./data/Image/temp_oil.png"),
                        File("./data/Image/bg_oil.png")
                    )
                    val inputStream = File("./data/Image/temp_oil.png").toExternalResource()
                    val id = group.uploadImage(inputStream).imageId
                    withContext(Dispatchers.IO) { inputStream.close() }
                    group.sendMessage(Image(id))
                } else if (cmd == "全国油价文字") {
                    group.sendMessage(getOil("全国"))
                } else if ("油价" in cmd) {
                    group.sendMessage(getOil(cmd.replaceFirst("油价", "")))
                } else if (cmd.startsWith("舔")) {
                    val user = searchFirstUserByAt(message)
                    if (user == null) group.sendMessage(message.quote() + "要我舔谁呢？")
                    else {
                        val (msg, result) = lick()
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else group.sendMessage(At(user).followedBy(PlainText(result)))
                    }
                } else if ("词云" in cmd) {
                    val result = getGroupWordCloud(group.id)
                    if (result == "Success") {
                        val inputStream = File("./data/${group.id}.png").toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        group.sendMessage(Image(id))
                        withContext(Dispatchers.IO) { inputStream.close() }
                    } else group.sendMessage(
                        message.quote() + result.replace(
                            Regex(".:\\\\.*\\..."),
                            "***"
                        )
                    )
                } else if (cmd == "cos") {
                    if (sender.id !in Administrator.administrators) group.sendMessage(message.quote() + "对不起，您没有权限使用该功能")
                    else {
                        val (msg, result) = getCoser()
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else {
                            val inputStream = File(result).toExternalResource()
                            val id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        }
                    }
                } else if (cmd == "cat") {
                    val (msg, result) = getCat()
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(result).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        group.sendMessage(Image(id))
                    }
                } else if (cmd == "陪我聊天") {
                    User.conversationLock[sender.id] = true
                    group.sendMessage(message.quote() + "好的哦！当你不想跟我聊天的时候跟我说“不聊了”就可以了！")
                    whileSelectMessages {
                        "不聊了" {
                            group.sendMessage(message.quote() + "好~下次再说！")
                            false
                        }
                        default {
                            val msg = kotlin.runCatching { message.filterIsInstance<PlainText>().joinToString("") }
                                .getOrNull()
                                ?.replace(Regex("\\s"), "")
                            if (msg == null || msg == "") group.sendMessage(message.quote() + "哥哥你说句话呀！")
                            else {
                                val (code, result) = chat(msg)
                                if (!code) group.sendMessage(message.quote() + result)
                                else {
                                    val inputStream = File(result).toExternalResource()
                                    val audio = group.uploadAudio(inputStream)
                                    withContext(Dispatchers.IO) { inputStream.close() }
                                    group.sendMessage(audio)
                                }
                            }
                            true
                        }
                    }
                } else if (cmd == "news") {
                    val (msg, result) = getNews()
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(result).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        group.sendMessage(Image(id))
                    }
                } else if (cmd.startsWith("文章")) {
                    group.sendMessage(message.quote() + getArticle(cmd.replaceFirst("文章", "")))
                } else if (cmd.startsWith("en")) {
                    group.sendMessage(message.quote() + encode(cmd.replaceFirst("en", "")))
                } else if (cmd.startsWith("de")) {
                    group.sendMessage(message.quote() + decode(cmd.replaceFirst("de", "")))
                } else if ("是什么垃圾" in cmd) {
                    group.sendMessage(message.quote() + trash(cmd.replace("是什么垃圾", "")))
                } else if (cmd == "摸鱼") {
                    val (msg, result) = getCalendar()
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(result).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        group.sendMessage(Image(id))
                    }
                } else if ("明天天气" in cmd) {
                    val city = cmd.replace("明天天气", "")
                    val (msg, result) = getLocation(city)
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else group.sendMessage(message.quote() + getDailyWeather(result, city, 1))
                } else if ("后天天气" in cmd) {
                    val city = cmd.replace("后天天气", "")
                    val (msg, result) = getLocation(city)
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else group.sendMessage(message.quote() + getDailyWeather(result, city, 2))
                } else if ("天气" in cmd) {
                    val city = cmd.replace("天气", "")
                    val (msg, result) = getLocation(city)
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else group.sendMessage(
                        message.quote() + getNowWeather(result, city) + "\n" + getDailyWeather(result, city, 0)
                    )
                } else if (cmd.startsWith("爬")) {
                    val user = searchFirstUserByAt(message)
                    if (user == null) group.sendMessage(message.quote() + "你想让谁爬，@ta吧！")
                    else {
                        val (msg, result) = crawl(user)
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else {
                            val inputStream = File(result).toExternalResource()
                            val id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        }
                    }
                } else if (cmd.startsWith("赞")) {
                    val user = searchFirstUserByAt(message)
                    if (user == null) group.sendMessage(message.quote() + "@你想赞的人吧！")
                    else {
                        val (msg, result) = like(user)
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else {
                            val inputStream = File(result).toExternalResource()
                            val id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        }
                    }
                } else if (cmd.startsWith("丢")) {
                    val user = searchFirstUserByAt(message)
                    if (user == null) group.sendMessage(message.quote() + "@你想丢的人吧！")
                    else {
                        val (msg, result) = diu(user)
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else {
                            val inputStream = File(result).toExternalResource()
                            val id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        }
                    }
                } else if (cmd == "买家秀") {
                    if (sender.id !in Administrator.administrators) group.sendMessage(message.quote() + "对不起，您没有权限使用该功能")
                    else {
                        val (msg, result) = getBuyerShow()
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else {
                            val inputStream = File(result).toExternalResource()
                            val id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        }
                    }
                } else if (cmd == "美女") {
                    if (sender.id !in Administrator.administrators) group.sendMessage(message.quote() + "对不起，您没有权限使用该功能")
                    else {
                        val (msg, result) = getBeauty()
                        if (result == null) group.sendMessage(message.quote() + msg)
                        else {
                            val inputStream = File(result).toExternalResource()
                            val id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) { inputStream.close() }
                            group.sendMessage(Image(id))
                        }
                    }
                } else if (cmd.startsWith("双色球")) {
                    group.sendMessage(message.quote() + unionLotto(cmd.replace("双色球", "").trim()))
                } else if (cmd.startsWith("二维码")) {
                    val (msg, result) = qrCode(cmd.replaceFirst("二维码", ""), "temp_${sender.id}")
                    if (result == null) group.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(result).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        group.sendMessage(message.quote() + Image(id))
                    }
                } else if (cmd == "猜成语") {
                    User.conversationLock[sender.id] = true
                    val (success, answer, path) = guessIdiom()
                    if (success && User.getUser(sender).energyValue >= 20) {
                        val inputStream = File(path!!).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        group.sendMessage(message.quote() + Image(id))
                        withContext(Dispatchers.IO) {
                            inputStream.close()
                        }
                        var times = 3
                        whileSelectMessages {
                            "猜不出来" {
                                addEnergy(sender, -20)
                                group.sendMessage(
                                    message.quote() + "答案是: ${answer}, 再接再厉哦! 本次游戏花费能量值20! 你目前有${
                                        User.getUser(
                                            sender
                                        ).energyValue
                                    }点能量值!"
                                )
                                false
                            }
                            answer {
                                val reward = (1..50).random()
                                addEnergy(sender, reward)
                                group.sendMessage(message.quote() + "恭喜你答对啦! 奖励你${reward}点能量值! 你目前有${User.getUser(sender).energyValue}点能量值!")
                                false
                            }
                            default {
                                times--
                                if (times > 0) {
                                    group.sendMessage(message.quote() + "不对哦! 你还有${times}次机会! 你也可以说\"猜不出来\"来结束猜成语游戏!")
                                    true
                                } else {
                                    addEnergy(sender, -20)
                                    group.sendMessage(
                                        message.quote() + "不对哦! 你已经没有机会了, 正确答案是: ${answer}! 本次游戏花费能量值20! 你目前有${
                                            User.getUser(
                                                sender
                                            ).energyValue
                                        }点能量值!"
                                    )
                                    false
                                }
                            }
                        }
                    } else if (User.getUser(sender).energyValue < 20) group.sendMessage(message.quote() + "你的能量值不足20, 无法参与游戏!")
                    else group.sendMessage(message.quote() + answer)
                } else if (cmd == "成语接龙") {
                    val (success, idiom, _) = guessIdiom(justIdiom = true)
                    if (success) {
                        group.sendMessage(
                            "成语接龙游戏开始! 本次游戏以\"${idiom}\"开始! 请说出以\"${getPinYin(idiom)}\"开头的成语以继续游戏! " +
                                "我会对你所说的话进行简单的判断来确认是否可以继续游戏! 随时可以通过\"不玩了\"来结束游戏哦~"
                        )
                        IdiomSolitaire.gameMap[group.id] = true
                        IdiomSolitaire.keyMap[group.id] = LinkedList<String>().apply { addFirst(idiom) }
                    } else group.sendMessage("游戏开始失败! 请稍后再试~")
                } else {
                    val result = Help.searchCommand(cmd.lowercase(), Help.funcGroup)
                    if (result != null) group.sendMessage(message.quote() + result)
                    else group.sendMessage(message.quote() + "不知道要做什么的话请说\"kgghelp\"!")
                }
            }
            //与kgg聊天
            else if (searchFirstUserByAt(message) in Administrator.botList) {
                val msg = kotlin.runCatching { message.filterIsInstance<PlainText>().joinToString("") }
                    .getOrNull()
                    ?.replace(Regex("\\s"), "")
                if (msg == null || msg == "") group.sendMessage(message.quote() + "哥哥你说句话呀！")
                else {
                    val (code, result) = chat(msg)
                    if (!code) group.sendMessage(message.quote() + result)
                    else {
                        val inputStream = File(result).toExternalResource()
                        val audio = group.uploadAudio(inputStream)
                        withContext(Dispatchers.IO) { inputStream.close() }
                        group.sendMessage(audio)
                    }
                }
            }
            //处理闪照
//            message.filterIsInstance<FlashImage>().forEach {
//                group.sendMessage(it.image + PlainText("\n下载地址: ${it.image.queryUrl()}"))
//            }
            /*
            if (message.contentToString() == "hi") {
                //群内发送
                group.sendMessage("hi")
                //向发送者私聊发送消息
                sender.sendMessage("hi")
                //不继续处理
                return@subscribeAlways
            }
            //分类示例
            message.forEach {
                //循环每个元素在消息里
                if (it is Image) {
                    //如果消息这一部分是图片
                    val url = it.queryUrl()
                    group.sendMessage("图片，下载地址$url")
                }
                if (it is PlainText) {
                    //如果消息这一部分是纯文本
                    group.sendMessage("纯文本，内容:${it.content}")
                }
            }
            */
            //解除每一次触发的对话锁
            User.conversationLock[sender.id] = false
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
            //黑名单和对话锁
            if (sender.id in Administrator.blacklist || User.conversationLock[sender.id] == true) return@subscribeAlways
            if (User.weatherLock[sender.id] == true) {
                User.weatherLock[sender.id] = false
                val location = message.contentToString()
                if ("lon=" in location && "lat=" in location) {
                    val lon = location.indexOf("lon=") + 4
                    val lat = location.indexOf("lat=") + 4
                    val loc = location.substring(lon, lon + 6) + "," + location.substring(lat, lat + 5)
                    sender.sendMessage(message.quote() + getNowWeather(loc))
                } else if ("lng\":" in location && "lat\":" in location) {
                    val lon = location.indexOf("lng\":") + 6
                    val lat = location.indexOf("lat\":") + 6
                    val loc = location.substring(lon, lon + 6) + "," + location.substring(lat, lat + 5)
                    sender.sendMessage(message.quote() + getNowWeather(loc))
                } else sender.sendMessage(message.quote() + "这不是一个定位，请重试！\n")
                return@subscribeAlways
            }
            //屏蔽机器人本人消息无限循环
            if (sender.id != bot.id) {
                //好友信息
                //管理员命令
                if (sender.id in Administrator.administrators && message.contentToString().startsWith("**")) {
                    if (message.contentToString().replaceFirst("**", "").startsWith("ad")) {
                        for (group in bot.groups) group.sendMessage(
                            message.contentToString().replaceBefore("ad", "").replace("ad", "")
                        )
                        sender.sendMessage("公告已发送成功")
                    }
                }
                val result = sign(sender, 1.0)
                if (result != "") sender.sendMessage(result)
                if (message.contentToString() == "查询状态") {
                    sender.sendMessage(queryStatus(sender))
                } else if (message.contentToString() == "help" || message.contentToString() == "kgghelp") {
                    val inputStream = File("./data/Image/help/help.png").toExternalResource()
                    val id = sender.uploadImage(inputStream).imageId
                    withContext(Dispatchers.IO) { inputStream.close() }
                    sender.sendMessage(Image(id))
//                    textToPicture(
//                        "私聊功能：\n\n" + Help.toString(Help.funcFriend).trim(),
//                        Font("等线", Font.PLAIN, 50),
//                        File("./data/Image/temp_help.png"),
//                        File("./data/Image/bg_help.png")
//                    )
//                    val inputStream = File("./data/Image/temp_help.png").toExternalResource()
//                    val id = sender.uploadImage(inputStream).imageId
//                    withContext(Dispatchers.IO) { inputStream.close() }
//                    sender.sendMessage(Image(id))
                } else if (message.contentToString().startsWith("h") || message.contentToString().startsWith("kggh")) {
                    var name = message.contentToString().replace("kggh", "").replace("h", "")
                    if (name == "") name = "0"
                    try {
                        val inputStream = File("./data/Image/help/$name.png").toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    } catch (_: Exception) {
                        sender.sendMessage(message.quote() + "请输入合法的功能序号哦，不知道序号的话请说\"kgghelp\"")
                    }
                } else if (message.contentToString().startsWith("dice")) {
                    val n = message.contentToString().replace("dice", "").toIntOrNull()
                    if (n != null && n > 0) sender.sendMessage("你roll出了${(1..n).random()}")
                    else sender.sendMessage("看不懂你要抽到多少哦, 请尝试大于1的整数!")
                } else if (message.contentToString() == "cos") {
                    val (msg, r) = getCoser()
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    }
                } else if (message.contentToString() == "cat") {
                    val (msg, r) = getCat()
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    }
                } else if (message.contentToString() == "陪我聊天") {
                    User.conversationLock[sender.id] = true
                    sender.sendMessage("好的哦！当你不想跟我聊天的时候跟我说“不聊了”就可以了！")
                    whileSelectMessages {
                        "不聊了" {
                            sender.sendMessage("好~下次再说！")
                            false
                        }
                        default {
                            val msg = kotlin.runCatching { message.filterIsInstance<PlainText>().joinToString("") }
                                .getOrNull()
                                ?.replace(Regex("\\s"), "")
                            if (msg == null || msg == "") sender.sendMessage("哥哥你说句话呀！")
                            else {
                                val (code, r) = chat(msg)
                                if (!code) sender.sendMessage(r)
                                else {
                                    val inputStream = File(r).toExternalResource()
                                    val audio = sender.uploadAudio(inputStream)
                                    withContext(Dispatchers.IO) { inputStream.close() }
                                    sender.sendMessage(audio)
                                }
                            }
                            true
                        }
                    }
                } else if (message.contentToString() == "news") {
                    val (msg, r) = getNews()
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    }
                } else if (message.contentToString().startsWith("来点")) {
                    val id: String
                    val (msg, r) = getRandomPixivPic(message.contentToString().replaceFirst("来点", ""))
                    if (r == "./data/Image/temp_pixiv.jpg" || r == "./data/Image/temp_pixiv.png") {
                        val inputStream = File(r).toExternalResource()
                        id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    } else if (r != null) sender.sendMessage(message.quote() + "$msg\n$r")
                    else sender.sendMessage(message.quote() + msg)
                } else if (message.contentToString().startsWith("metar")) {
                    if (message.contentToString() == "metar")
                        sender.sendMessage(message.quote() + "你要查询哪个机场呢，在后面加上它的ICAO代码吧~")
                    else
                        sender.sendMessage(
                            message.quote() + getMetar(
                                message.contentToString().replaceFirst("metar", "")
                            )
                        )
                } else if (message.contentToString() == "天气") {
                    User.weatherLock[sender.id] = true
                    sender.sendMessage(message.quote() + "请发送定位!")
                } else if (message.contentToString() == "全国油价") {
                    textToPicture(
                        getOil("全国"),
                        Font("等线", Font.PLAIN, 50),
                        File("./data/Image/temp_oil.png"),
                        File("./data/Image/bg_oil.png")
                    )
                    val inputStream = File("./data/Image/temp_oil.png").toExternalResource()
                    val id = sender.uploadImage(inputStream).imageId
                    withContext(Dispatchers.IO) { inputStream.close() }
                    sender.sendMessage(Image(id))
                } else if (message.contentToString() == "全国油价文字") {
                    sender.sendMessage(getOil("全国"))
                } else if ("油价" in message.contentToString()) {
                    sender.sendMessage(getOil(message.contentToString().replaceFirst("油价", "")))
                } else if (message.contentToString().startsWith("文章")) {
                    sender.sendMessage(message.quote() + getArticle(message.contentToString().replaceFirst("文章", "")))
                } else if (message.contentToString().startsWith("en")) {
                    sender.sendMessage(message.quote() + encode(message.contentToString().replaceFirst("en", "")))
                } else if (message.contentToString().startsWith("de")) {
                    sender.sendMessage(message.quote() + decode(message.contentToString().replaceFirst("de", "")))
                } else if ("是什么垃圾" in message.contentToString()) {
                    sender.sendMessage(message.quote() + trash(message.contentToString().replace("是什么垃圾", "")))
                } else if (message.contentToString() == "摸鱼") {
                    val (msg, r) = getCalendar()
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    }
                } else if ("明天天气" in message.contentToString()) {
                    val city = message.contentToString().replace("明天天气", "")
                    val (msg, r) = getLocation(city)
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else sender.sendMessage(message.quote() + getDailyWeather(r, city, 1))
                } else if ("后天天气" in message.contentToString()) {
                    val city = message.contentToString().replace("后天天气", "")
                    val (msg, r) = getLocation(city)
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else sender.sendMessage(message.quote() + getDailyWeather(r, city, 2))
                } else if ("天气" in message.contentToString()) {
                    val city = message.contentToString().replace("天气", "")
                    val (msg, r) = getLocation(city)
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else sender.sendMessage(
                        message.quote() + getNowWeather(r, city) + "\n" + getDailyWeather(r, city, 0)
                    )
                } else if (message.contentToString() == "买家秀") {
                    val (msg, r) = getBuyerShow()
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    }
                } else if (message.contentToString() == "美女") {
                    val (msg, r) = getBeauty()
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(Image(id))
                    }
                } else if (message.contentToString().startsWith("申请解除禁言 ")) {
                    val groupstr = message.contentToString().replace("申请解除禁言 ", "").replaceAfter(" ", "").trim()
                    val newname = message.contentToString().replace("申请解除禁言 ", "").replaceBefore(" ", "").trim()
                    sender.sendMessage(PlainText(unmute(bot, newname, groupstr, sender.id)))
                } else if (message.contentToString().startsWith("匿名消息")) {
                    sendAnonymousMessage(bot, sender, message)
                } else if (message.contentToString().startsWith("Reply")) {
                    replyAnonymousMessage(bot, sender, message)
                } else if (message.contentToString() == "td") {
                    refuse(sender, bot)
                } else if (message.contentToString() == "xd") {
                    accept(sender)
                } else if (message.contentToString().startsWith("双色球")) {
                    sender.sendMessage(
                        message.quote() + unionLotto(
                            message.contentToString().replace("双色球", "").trim()
                        )
                    )
                } else if (message.contentToString().startsWith("二维码")) {
                    val (msg, r) = qrCode(message.contentToString().replaceFirst("二维码", ""), "temp_${sender.id}")
                    if (r == null) sender.sendMessage(message.quote() + msg)
                    else {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        withContext(Dispatchers.IO) { inputStream.close() }
                        sender.sendMessage(message.quote() + Image(id))
                    }
                } else if (message.contentToString().startsWith("我今天") && "吃什么" in message.contentToString()) {
                    var type = message.contentToString().replace("我今天", "")
                        .replaceAfter("吃什么", "").replace("吃什么", "")
                    var n: Int? = 1
                    var temp = message.contentToString().replaceBefore("吃什么", "").replaceFirst("吃什么", "")
                    if ("x" in message.contentToString()) {
                        n = message.contentToString().replaceBefore("x", "").replace("x", "").toIntOrNull()
                        temp = temp.replaceAfterLast("x", "").substringBeforeLast("x")
                    }
                    if (type == "" || temp != "") type = temp
                    if (n == 1) sender.sendMessage(message.quote() + randomEat(type))
                    else if (n != null && n in 2..10) sender.sendMessage(message.quote() + randomEat(type, n!!))
                    else sender.sendMessage(message.quote() + "重复抽取命令格式错误! 请尝试2-10的整数!")
                } else if (message.contentToString().startsWith("mix")) {
                    val r = emojiMix(message.contentToString().replace("mix", ""))
                    if (r.startsWith("./data/Image/")) {
                        val inputStream = File(r).toExternalResource()
                        val id = sender.uploadImage(inputStream).imageId
                        sender.sendMessage(Image(id))
                        withContext(Dispatchers.IO) { inputStream.close() }
                    } else sender.sendMessage(r)
                } else if (message.contentToString().startsWith("python")) {
                    val (r, _) = runPython(message.contentToString())
                    try {
                        if (r != null && r != "") sender.sendMessage(
                            message.quote() + PlainText(
                                r.replace(
                                    Regex(".:\\\\.*\\..."),
                                    "**此为机密领域, 妄图窥探的话是会被关小黑屋的**"
                                )
                            )
                        )
                        else if (r != null) sender.sendMessage(message.quote() + PlainText("运行结果为空!"))
                        else sender.sendMessage(message.quote() + PlainText("Error: TLE"))
                    } catch (e: Exception) {
                        sender.sendMessage(message.quote() + PlainText(e.message ?: "Error: RE"))
                    }
                } else {
                    val r = Help.searchCommand(message.content.lowercase(), Help.funcFriend)
                    if (r != null) sender.sendMessage(r)
                    else sender.sendMessage("不知道要做什么的话请说\"help\"!")
                }
            }
            //解除每一次触发的对话锁
            User.conversationLock[sender.id] = false
        }
        eventChannel.subscribeAlways<NewFriendRequestEvent> {
            //自动同意好友申请
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            //自动同意加群申请
            //accept()
        }
        eventChannel.subscribeAlways<FriendAddEvent> {
            it.friend.sendMessage("很高兴认识你！我是kgg~")
            textToPicture(
                "群聊功能：\n\n" + Help.toString(Help.funcGroup).trim(),
                Font("等线", Font.PLAIN, 50),
                File("./data/Image/temp_kgghelp.png"),
                File("./data/Image/bg_help.png")
            )
            var inputStream = File("./data/Image/temp_kgghelp.png").toExternalResource()
            var id = it.friend.uploadImage(inputStream).imageId
            withContext(Dispatchers.IO) { inputStream.close() }
            it.friend.sendMessage(Image(id))
            textToPicture(
                "私聊功能：\n\n" + Help.toString(Help.funcFriend).trim(),
                Font("等线", Font.PLAIN, 50),
                File("./data/Image/temp_help.png"),
                File("./data/Image/bg_help.png")
            )
            inputStream = File("./data/Image/temp_help.png").toExternalResource()
            id = it.friend.uploadImage(inputStream).imageId
            withContext(Dispatchers.IO) { inputStream.close() }
            it.friend.sendMessage(Image(id))
        }
        eventChannel.subscribeAlways<MemberCardChangeEvent> {
            member.nameCard = new
            //group.sendMessage(PlainText("群成员名片变更为")+PlainText(member.nameCard))
        }
        eventChannel.subscribeAlways<GroupTempMessageEvent> {
            if (message.contentToString() == "help") {
                sender.sendMessage(Help.toString(Help.funcTemp).trim())
            } else if (message.contentToString().startsWith("申请解除禁言 ")) {
                val newname = message.contentToString().replace("申请解除禁言 ", "").trim()
                sender.sendMessage(
                    PlainText(
                        unmute(bot, newname, group.id.toString(), sender.id)
                    )
                )
            } else {
                sender.sendMessage("不知道要做什么的话请说\"help\"!")
            }
        }
    }
}
