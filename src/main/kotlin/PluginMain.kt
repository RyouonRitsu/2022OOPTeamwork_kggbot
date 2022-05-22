package org.ritsu.mirai.plugin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.selectMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import org.ritsu.mirai.plugin.commands.*
import org.ritsu.mirai.plugin.commands.translate.NotAvailable
import org.ritsu.mirai.plugin.commands.translate.languageType
import org.ritsu.mirai.plugin.commands.translate.translate
import org.ritsu.mirai.plugin.entity.*
import org.ritsu.mirai.plugin.kernel.searchUserByAt
import java.io.File

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
            //群消息
            //管理员命令
            if (sender.id in Administrator.administrators && message.contentToString().startsWith("**")) {
                if (message.contentToString().contains("能量值")) {
                    group.sendMessage(
                        adjustUserEnergy(
                            searchUserByAt(message),
                            message.contentToString().replaceBefore("能量值", "").replace("能量值", "").toIntOrNull()
                        )
                    )
                } else if (message.contentToString().contains("查询")) {
                    group.sendMessage(queryUserEnergy(searchUserByAt(message)))
                }
            }
            //普通命令
            //复读示例
            if (message.contentToString().startsWith("复读")) {
                group.sendMessage(message.contentToString().replace("复读", ""))
            }
            //kgg命令
            if (
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
                    sender.sendMessage(Help.toString(Help.funcGroup).trim())
                } else if (cmd.startsWith("我今天") && cmd.contains("吃什么")) {
                    var type = cmd.replace("我今天", "")
                        .replaceAfter("吃什么", "").replace("吃什么", "")
                    var n: Int? = 1
                    var temp = cmd.replaceBefore("吃什么", "").replaceFirst("吃什么", "")
                    if (cmd.contains("x")) {
                        n = cmd.replaceBefore("x", "").replace("x", "").toIntOrNull()
                        temp = temp.replaceAfterLast("x", "").substringBeforeLast("x")
                    }
                    if (type == "" || temp != "") {
                        type = temp
                    }
                    if (n == 1) {
                        group.sendMessage(message.quote() + randomEat(type))
                    } else if (n != null && n in 2..10) {
                        group.sendMessage(message.quote() + randomEat(type, n))
                    } else group.sendMessage(message.quote() + "重复抽取命令格式错误! 请尝试2-10的整数!")
                } else if (cmd == "吃的类型") {
                    group.sendMessage(dishLs())
                } else if (cmd.startsWith("mix")) {
                    val result = emojiMix(cmd.replace("mix", ""))
                    if (result.startsWith("./data/Image/")) {
                        val inputStream = File(result).toExternalResource()
                        val id = group.uploadImage(inputStream).imageId
                        group.sendMessage(Image(id))
                        withContext(Dispatchers.IO) {
                            inputStream.close()
                        }
                    } else group.sendMessage(result)
                } else if (cmd.startsWith("dice")) {
                    val n = cmd.replace("dice", "").toIntOrNull()
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
                    var flag = true
                    var id: String? = null
                    message.filterIsInstance<Image>().forEach {
                        val (result, msg) = searchImageSource(it.queryUrl())
                        if (msg == "./data/Image/temp_thumbnail.png") {
                            val inputStream = File(msg).toExternalResource()
                            id = group.uploadImage(inputStream).imageId
                            withContext(Dispatchers.IO) {
                                inputStream.close()
                            }
                        }
                        if (id != null) group.sendMessage(message.quote() + Image(id!!) + "\n$result")
                        else group.sendMessage(message.quote() + "找到如下结果:\n$result")
                        flag = false
                    }
                    if (flag) {
                        group.sendMessage(message.quote() + "请在30秒内发送图片或图片链接!")
                        val imageUrl = selectMessages {
                            has<Image> { it.queryUrl() }
                            has<PlainText> { it.content }
                            default { "请发送图片或图片链接!" }
                            timeout(30_000) { "timeout" }
                        }
                        if (imageUrl == "timeout") group.sendMessage(At(sender).followedBy(PlainText("超时了, 请重试!")))
                        else if (imageUrl.startsWith("http")) {
                            val (result, msg) = searchImageSource(imageUrl)
                            if (msg == "./data/Image/temp_thumbnail.png") {
                                val inputStream = File(msg).toExternalResource()
                                id = group.uploadImage(inputStream).imageId
                                withContext(Dispatchers.IO) {
                                    inputStream.close()
                                }
                            }
                            if (id != null) group.sendMessage(At(sender).followedBy(Image(id!!) + PlainText("\n$result")))
                            else group.sendMessage(At(sender).followedBy(PlainText("找到如下结果:\n$result")))
                        } else group.sendMessage(At(sender).followedBy(PlainText(imageUrl)))
                    }
                } else {
                    group.sendMessage(message.quote() + "不知道要做什么的话请说\"kgghelp\"!")
                }
            }
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
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
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
                } else if (message.contentToString() == "help") {
                    sender.sendMessage(Help.toString(Help.funcFriend).trim())
                } else if (message.contentToString().startsWith("dice")) {
                    val n = message.contentToString().replace("dice", "").toIntOrNull()
                    if (n != null && n > 0) sender.sendMessage("你roll出了${(1..n).random()}")
                    else sender.sendMessage("看不懂你要抽到多少哦, 请尝试大于1的整数!")
                } else {
                    sender.sendMessage("不知道要做什么的话请说\"help\"!")
                }
            }
        }
        eventChannel.subscribeAlways<NewFriendRequestEvent> {
            //自动同意好友申请
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            //自动同意加群申请
            //accept()
        }
    }
}
