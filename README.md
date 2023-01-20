# mirai-console-plugin-2022OOP大作业项目

> 开发人员: 周洪熙、卢嘉美、王晨宇
>
> 联系方式: 1780645196(QQ)

[toc]

## 声明

[Mirai Console](https://github.com/mamoe/mirai-console) 使用 Kotlin + Gradle.

## 主要功能

1. 抽卡*

    使用方式：“抽卡”

2. 决定吃什么

    使用方式：“我今天吃什么”

    如果想返回多种食物，可以在后面加上1~10的数，如“我今天吃什么x10”

    查询食物类型可以说：“吃的类型”

3. emoji合并

    使用方式：mix后面跟上两个emoji

    如：”mix💩💩“

4. pixiv图片

    使用方式：“来点”

    默认非r18，如需r18可以加上“r18”，“mix”表示二者随机

    支持加tag，tag之间以“&”隔开

    如：“来点r18”、“来点mix”、“来点r18萝莉&黑丝”、“来点白丝”

5. 占卜*

    使用方式：“占卜一下”+想要占卜的事件

    如：“占卜一下我会不会挂科”

6. 狗屁不通文章

    使用方式：“文章”+文章主体+字数

    文章主体和字数间用“&”隔开，不说明字数默认为200字

    如：“文章手机&100”

7. 买家秀

    使用方式：“买家秀”

8. 爬*

    使用方式：“爬”+@一位用户

9. 词云*

    使用方式：“今日词云”

10. 舔人*

    使用方式：“舔”+@一位用户

11. 点赞*

    使用方式：“赞”+@一位用户

12. 摸鱼日历

    使用方式：“摸鱼”

13. 丢人*

    使用方式：“丢”+@一位用户
14. 聊天

    使用方式：直接@kgg或者说“陪我聊天”

15. coser

    使用方式：“cos”

16. 美女

    使用方式：“美女”

17. 猫猫

    使用方式：“cat”

18. 双色球

    使用方式：“双色球”+7个号码

    其中前六个是红球，最后一个是篮球，红球号码为1~33，蓝球号码为1~16，均不能重复，号码之间以一个空格隔开

    如：“双色球1 2 3 4 5 6 7”

19. 猜成语*

    使用方式：“猜成语”

20. 成语接龙*

    使用方式：“成语接龙”

21. 匿名消息

    该功能仅私聊可用

    发送匿名消息：“匿名消息-”+对方QQ号+“：”+想说的话

    如：“匿名消息-10000：你好”

    回复匿名消息：“Reply-”+消息编号+“：”+想说的话

    如：“Reply-1：你好”

    拒绝继续接收任何匿名消息：“td”

    开启接收匿名消息权限：“xd”

## 实用工具

1. 随机数

    使用方式：“dice”+随机数上限

    如：“dice100”会返回1~100中随机一个整数

2. 翻译*

    使用方式：“t”+翻译内容+语言

    不声明语言将自动识别语言

    如：“thello”、“t你好->英语”、“tBonjour->日语”

    发送“支持语言”可以得到支持语言列表

3. 搜图*

    该功能仅支持在pixiv上查找图片

    使用方式：“搜图”+一张图片

4. python

    使用方式：“python”+代码

    如：

    “pythonprint(1)”

5. 气象报文

    使用方式：“metar”+机场ICAO代码

    如：“metarzbaa”

6. 生成二维码

    使用方式：“二维码”+二维码的内容

    如：“二维码你好”

7. 新闻

    使用方式：“news”

8. 加密/解密文字

    加密：“en”+内容+key，内容和key之间用“&”隔开

    其中key为1~9位数字

    如：“en你好&123456”

    解密：“de”+内容+正确的key，内容和key之间用“&”隔开

    如：“deImN0eDZUa0FXVVF6bHNQODV4Z1ZZRVE9PSI=&123456”

9. 垃圾分类

    使用方式：垃圾名+“是什么垃圾”

    如：“餐巾纸是什么垃圾”

10. 天气

    使用方式：

    (1)发送“天气”，并发送一个定位

    (2)城市名+“天气”

    如：“北京天气”

    支持查询明后天的天气

    如：“北京明天天气”、“北京后天天气”

11. 油价

    使用方式：

    (1)中国内地省级行政区+“油价”

    如：“北京油价”、“吉林油价”

    (2)查询全国油价可以说：“全国油价”

    如需文字版可以说：“全国油价文字”

## 管理员可用功能

1. @群成员

    此功能用于在已知群成员某个信息如学号或姓名，且群昵称包含该信息的情况下，通过导出名单由bot完成@功能

    使用方式：

    "!!!" + 需要被@的群成员的部分或全部群昵称 + "："+需要发送的消息

    如：“!!!Alice , Bob:通知内容”

    注：多位群成员之间以“,”间隔

2. 禁言群成员

    此功能用于禁言群昵称不符合规范的群成员

    使用方式:

    (1)"禁言群成员 " + 规定格式的正则表达式 (+ 禁言时间)

    如："禁言群成员 \d{6}-[\u4e00-\u9fa5_a-zA-Z0-9_]{2,10} 800"、"禁言群成员 \d{6}-[\u4e00-\u9fa5_a-zA-Z0-9_]{2,10}"

    (2)"禁言群成员 空" (+ 禁言时间)

    "空"表示使用上一次调用此功能时设置的正则表达式进行筛选

3. 清理群成员

    此功能用于清理群昵称不符合规范的群成员

    使用方式：

    (1)"清理群成员 " + 规定格式的正则表达式

    如："清理群成员 \d{6}-[\u4e00-\u9fa5_a-zA-Z0-9_]{2,10}"

    (2)"清理群成员 空"

    "空"表示使用上一次调用此功能时设置的正则表达式进行筛选
