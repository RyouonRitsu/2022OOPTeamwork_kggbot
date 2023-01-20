package org.ritsu.mirai.plugin.commands

import com.github.binarywang.java.emoji.EmojiConverter
import java.net.HttpURLConnection
import java.net.URL

/**
 * 支持的Emoji数据类
 *
 * @author RyouonRitsu
 * @since 0.1.0
 * @property code emoji编码
 * @property str emoji对应Api中的存放位置
 * @property info emoji信息
 */
data class Emoji(val code: List<Int>, val str: String, val info: List<String>) {
    companion object {
        /**
         * 主Api网址
         */
        const val API = "https://www.gstatic.com/android/keyboard/emojikitchen/"

        /**
         * 支持的Emoji集合
         */
        val emojis = listOf(
            Emoji(listOf(128516), "20201001", listOf("smile", "happy")),
            Emoji(listOf(128512), "20201001", listOf("smile", "happy")),
            Emoji(listOf(128578), "20201001", listOf("smile", "happy")),
            Emoji(listOf(128579), "20201001", listOf("smile", "happy", "upsidedown")),
            Emoji(listOf(128515), "20201001", listOf("smile", "happy")),
            Emoji(listOf(128513), "20201001", listOf("smile", "happy", "cheese", "teeth", "grin")),
            Emoji(listOf(128522), "20201001", listOf("smile", "happy", "innocent", "blush")),
            Emoji(listOf(9786, 65039), "20201001", listOf("smile", "happy", "innocent", "blush")),
            Emoji(listOf(128519), "20201001", listOf("smile", "happy", "innocent", "angel", "holy", "halo")),
            Emoji(listOf(128518), "20201001", listOf("smile", "happy", "laugh", "xd")),
            Emoji(listOf(128514), "20201001", listOf("smile", "happy", "laugh", "cry", "lol", "lmao", "rofl")),
            Emoji(listOf(129315), "20201001", listOf("smile", "happy", "laugh", "cry", "side", "lol", "lmao", "rofl")),
            Emoji(listOf(128517), "20201001", listOf("smile", "happy", "sweat", "nervous", "awkward", "uncomfortable")),
            Emoji(listOf(128521), "20201001", listOf("smile", "happy", "wink")),
            Emoji(listOf(128535), "20201001", listOf("kiss")),
            Emoji(listOf(128537), "20201001", listOf("kiss")),
            Emoji(listOf(128538), "20201001", listOf("kiss", "blush")),
            Emoji(listOf(128536), "20201001", listOf("kiss", "heart", "love", "wink")),
            Emoji(listOf(128525), "20201001", listOf("smile", "happy", "heart", "love", "eyes")),
            Emoji(listOf(129392), "20201001", listOf("smile", "happy", "heart", "love")),
            Emoji(listOf(129321), "20201001", listOf("smile", "happy", "star", "eyes")),
            Emoji(listOf(128539), "20201001", listOf("smile", "happy", "tongue")),
            Emoji(listOf(128541), "20201001", listOf("smile", "happy", "tongue", "laugh", "xd")),
            Emoji(listOf(128523), "20201001", listOf("smile", "happy", "tongue", "yum")),
            Emoji(listOf(128540), "20201001", listOf("smile", "happy", "tongue", "wink")),
            Emoji(listOf(129322), "20201001", listOf("smile", "happy", "tongue", "eyes", "joking", "funny")),
            Emoji(
                listOf(129297),
                "20201001",
                listOf("smile", "happy", "money", "dollar", "currency", "tongue", "eyes")
            ),
            Emoji(listOf(129394), "20201001", listOf("smile", "cry", "dying", "inside", "sad")),
            Emoji(listOf(129303), "20201001", listOf("smile", "happy", "blush", "hug", "hand")),
            Emoji(listOf(129323), "20201001", listOf("smile", "whisper", "shush", "quiet", "secret", "silent")),
            Emoji(listOf(129325), "20201001", listOf("blush", "embarrass", "hand", "cover", "quiet")),
            Emoji(listOf(129762), "20211115", listOf("hand", "cover", "quiet", "secret")),
            Emoji(listOf(129763), "20211115", listOf("hand", "cover", "eye", "peeking", "secret")),
            Emoji(listOf(129296), "20201001", listOf("quiet", "zip", "silent")),
            Emoji(listOf(128566), "20201001", listOf("speechless", "silent", "quiet", "no", "mouth")),
            Emoji(listOf(129300), "20201001", listOf("think", "ponder", "question", "thought", "confuse", "hand")),
            Emoji(listOf(129320), "20201001", listOf("confuse", "question", "suspicious", "distrust", "eyebrow")),
            Emoji(listOf(128528), "20201001", listOf("neutral", "blank", "straight", "bored")),
            Emoji(
                listOf(128529),
                "20201001",
                listOf("neutral", "blank", "straight", "bored", "eyes", "shut", "tired", "annoyed")
            ),
            Emoji(listOf(128566, 8205, 127787, 65039), "20210218", listOf("clouds", "peekaboo", "spy", "hidden")),
            Emoji(
                listOf(128527),
                "20201001",
                listOf("suspicious", "sus", "funtimes", "mischievous", "suggestive", "smirk", "smug")
            ),
            Emoji(
                listOf(128524),
                "20201001",
                listOf("happy", "satisf", "relaxed", "chill", "relieved", "calm", "peace", "thank")
            ),
            Emoji(listOf(128556), "20201001", listOf("uncomfortable", "awkward", "eek", "icky")),
            Emoji(listOf(128580), "20201001", listOf("rolling", "eyes", "omg", "done", "with", "life")),
            Emoji(
                listOf(128530),
                "20201001",
                listOf("annoyed", "grump", "side", "eye", "skeptic", "tired", "negative")
            ),
            Emoji(listOf(9785, 65039), "20201001", listOf("sad", "upset")),
            Emoji(
                listOf(128558, 8205, 128168),
                "20210218",
                listOf("sad", "tired", "depress", "sigh", "exhale", "air", "cloud")
            ),
            Emoji(listOf(128542), "20201001", listOf("sad", "depress", "upset", "disappoint")),
            Emoji(listOf(128532), "20201001", listOf("sad", "depress", "upset", "remorse")),
            Emoji(listOf(129317), "20201001", listOf("lie", "liar", "nose", "pinocchio", "disbelief")),
            Emoji(listOf(129393), "20201001", listOf("yawn", "tired", "bored", "sleepy", "uninterested")),
            Emoji(listOf(128554), "20201001", listOf("sleepy", "tired", "bored", "uninterested")),
            Emoji(listOf(128564), "20201001", listOf("sleepy", "tired", "bored", "uninterested", "z")),
            Emoji(listOf(129316), "20201001", listOf("happy", "smile", "drooling", "hunger", "hungry", "drool")),
            Emoji(
                listOf(128567),
                "20201001",
                listOf("sick", "mask", "covid", "corona", "virus", "hospital", "face", "cover")
            ),
            Emoji(listOf(129298), "20201001", listOf("sick", "fever", "thermometer", "heat", "warm", "hot", "sad")),
            Emoji(listOf(129301), "20201001", listOf("sick", "hurt", "bandage", "injury", "sad")),
            Emoji(listOf(129314), "20201001", listOf("sick", "green", "shrek", "vomit", "puke", "nausea")),
            Emoji(listOf(129326), "20201001", listOf("sick", "green", "vomit", "puke", "icky")),
            Emoji(listOf(129319), "20201001", listOf("sick", "nose", "tissue", "sad", "xd")),
            Emoji(listOf(129397), "20201001", listOf("warm", "hot", "heat", "horny", "sweat", "tongue", "red")),
            Emoji(
                listOf(129398),
                "20201001",
                listOf("cold", "freeze", "frozen", "chill", "snow", "snowflake", "ice", "teeth", "blue")
            ),
            Emoji(listOf(128565), "20201001", listOf("dead", "cross", "x", "eyes", "shook", "shock")),
            Emoji(
                listOf(129396),
                "20201001",
                listOf("happy", "smile", "drunk", "intoxicted", "woozy", "blush", "horny")
            ),
            Emoji(
                listOf(129760),
                "20211115",
                listOf("melt", "happy", "smile", "heat", "warm", "hot", "dying", "acceptance")
            ),
            Emoji(
                listOf(129327),
                "20201001",
                listOf("shook", "shock", "mindblow", "explosion", "eyes", "permanent", "brain", "damage")
            ),
            Emoji(listOf(129312), "20201001", listOf("happy", "cowboy", "hat")),
            Emoji(listOf(129395), "20201001", listOf("party", "hat", "happy", "celebrate")),
            Emoji(
                listOf(129400),
                "20201001",
                listOf("disguised", "detective", "glasses", "nose", "moustache", "mustache")
            ),
            Emoji(listOf(129488), "20201001", listOf("monocle", "frown", "curious", "detective")),
            Emoji(listOf(128526), "20201001", listOf("cool", "glasses", "sun", "hot")),
            Emoji(listOf(128533), "20201001", listOf("sad", "unsure", "hesitate", "frown", "disappoint")),
            Emoji(listOf(129764), "20211115", listOf("sad", "unsure", "hesitate", "frown", "disappoint")),
            Emoji(listOf(128543), "20201001", listOf("sad", "worry", "frown", "concern", "disappoint")),
            Emoji(listOf(128577), "20201001", listOf("sad", "upset")),
            Emoji(listOf(128558), "20201001", listOf("shook", "shock")),
            Emoji(listOf(128559), "20201001", listOf("shook", "shock", "astonish", "surprise")),
            Emoji(listOf(128562), "20201001", listOf("shook", "shock", "astonish", "surprise")),
            Emoji(listOf(128551), "20201001", listOf("shook", "shock", "astonish", "surprise", "worry")),
            Emoji(listOf(128550), "20201001", listOf("shook", "shock", "astonish", "surprise", "worry")),
            Emoji(listOf(128552), "20201001", listOf("fear", "cold", "worry", "shook", "shock", "concern")),
            Emoji(listOf(128560), "20201001", listOf("fear", "cold", "worry", "shook", "shock", "concern", "sweat")),
            Emoji(
                listOf(128561),
                "20201001",
                listOf("fear", "cold", "worry", "petrify", "shook", "shocked", "surprise", "haunt", "scared")
            ),
            Emoji(listOf(128563), "20201001", listOf("blush", "flush", "embarrass", "surprise")),
            Emoji(listOf(129761), "20211115", listOf("army", "salute", "hand")),
            Emoji(
                listOf(129765),
                "20211115",
                listOf("transparent", "faded", "fading", "dotted", "dashed", "see", "through")
            ),
            Emoji(listOf(129401), "20211115", listOf("happy", "tear", "cry", "puppy", "eyes")),
            Emoji(listOf(129402), "20201001", listOf("sad", "beg", "plead", "puppy", "eyes")),
            Emoji(
                listOf(129299),
                "20201001",
                listOf("nerd", "glasses", "smile", "smart", "intelligent", "harry", "potter")
            ),
            Emoji(listOf(128546), "20201001", listOf("sad", "tear", "cry")),
            Emoji(listOf(128557), "20201001", listOf("sad", "tear", "cry", "flood", "loud")),
            Emoji(listOf(128549), "20201001", listOf("sad", "sweat")),
            Emoji(listOf(128531), "20201001", listOf("sad", "sweat")),
            Emoji(listOf(128555), "20201001", listOf("loud", "scream", "shout", "moan")),
            Emoji(listOf(128553), "20201001", listOf("loud", "scream", "shout", "moan")),
            Emoji(listOf(128547), "20201001", listOf("sad", "uncomfortable", "icky")),
            Emoji(listOf(128534), "20201001", listOf("anger", "angry", "grr", "x", "eyes")),
            Emoji(listOf(128544), "20201001", listOf("anger", "angry", "grr")),
            Emoji(listOf(128545), "20201001", listOf("anger", "angry", "grr", "red")),
            Emoji(listOf(129324), "20201001", listOf("swear", "anger", "censor", "vulgar", "curse", "red")),
            Emoji(listOf(128548), "20201001", listOf("anger", "exhale", "cloud", "smoke", "bull")),
            Emoji(listOf(128520), "20201001", listOf("happy", "devil", "horn", "purple", "mischievous")),
            Emoji(listOf(128127), "20201001", listOf("angry", "devil", "horn", "purple", "annoyed")),
            Emoji(listOf(128169), "20201001", listOf("shit", "poop", "excrements", "brown", "smile")),
            Emoji(listOf(128128), "20201001", listOf("spook", "dead", "skull", "skeleton", "forgor", "funny")),
            Emoji(listOf(128125), "20201001", listOf("spook", "alien", "supernatural")),
            Emoji(listOf(128123), "20201001", listOf("spook", "ghost", "laugh", "boo", "tongue")),
            Emoji(listOf(129302), "20201001", listOf("spook", "robot", "machine", "teeth", "android")),
            Emoji(listOf(129313), "20201001", listOf("spook", "clown", "funny", "laugh", "blush", "fool")),
            Emoji(listOf(127875), "20201001", listOf("spook", "pumpkin", "jack", "lantern", "carve", "halloween")),
            Emoji(listOf(127801), "20201001", listOf("plant", "flower", "rose", "red")),
            Emoji(listOf(127804), "20201001", listOf("plant", "flower", "yellow")),
            Emoji(listOf(127799), "20201001", listOf("plant", "flower", "tulip", "pink")),
            Emoji(listOf(127800), "20210218", listOf("plant", "flower", "cherry", "blossom", "pink")),
            Emoji(listOf(128144), "20201001", listOf("plant", "flower", "bouquet")),
            Emoji(listOf(127797), "20201001", listOf("plant", "cactus", "spiky", "boi", "desert")),
            Emoji(listOf(127794), "20201001", listOf("plant", "tree", "christmas", "xmas", "spruce", "forest")),
            Emoji(listOf(129717), "20211115", listOf("plant", "tree", "wood", "log", "chop", "stump", "forest")),
            Emoji(listOf(127812), "20220406", listOf("mushroom", "shroom", "fungus", "amanita", "forest")),
            Emoji(listOf(129704), "20220406", listOf("rock", "stone", "boulder", "pebble")),
            Emoji(listOf(127821), "20201001", listOf("food", "fruit", "plant", "pineapple")),
            Emoji(listOf(129361), "20201001", listOf("food", "fruit", "plant", "vegetable", "avocado")),
            Emoji(
                listOf(127798, 65039),
                "20201001",
                listOf("food", "fruit", "plant", "vegetable", "pepper", "chilli", "red", "hot", "spicy", "jalapeno")
            ),
            Emoji(listOf(127820), "20211115", listOf("food", "fruit", "plant", "banana")),
            Emoji(listOf(127827), "20210831", listOf("food", "fruit", "plant", "strawberry")),
            Emoji(listOf(127819), "20210521", listOf("food", "fruit", "plant", "citrus", "lemon", "sour")),
            Emoji(listOf(127818), "20211115", listOf("food", "fruit", "plant", "citrus", "orange")),
            Emoji(listOf(127817), "20220406", listOf("food", "fruit", "watermelon", "melon")),
            Emoji(listOf(127826), "20220406", listOf("food", "fruit", "cherry")),
            Emoji(listOf(127874), "20201001", listOf("food", "sweet", "cake", "candle")),
            Emoji(listOf(129473), "20201001", listOf("food", "sweet", "cup", "cake", "sprinkle")),
            Emoji(listOf(129472), "20201001", listOf("food", "cheese")),
            Emoji(listOf(127789), "20201001", listOf("food", "hot", "dog")),
            Emoji(listOf(127838), "20210831", listOf("food", "bread", "bake", "yum", "chleb", "pyszny")),
            Emoji(listOf(9749), "20201001", listOf("food", "drink", "coffee", "hot")),
            Emoji(
                listOf(127869, 65039),
                "20201001",
                listOf("food", "plate", "dish", "fork", "knife", "cutlery", "kitchen")
            ),
            Emoji(
                listOf(129440),
                "20201001",
                listOf("covid", "corona", "virus", "microbe", "organism", "germ", "bacteria", "sick")
            ),
            Emoji(listOf(9924), "20201001", listOf("snow", "snowman", "cold", "ice", "hat", "christmas")),
            Emoji(listOf(127882), "20201001", listOf("confetti", "ball", "popper", "party", "celebrate", "pinata")),
            Emoji(listOf(127880), "20201001", listOf("balloon", "red", "fly")),
            Emoji(
                listOf(128142),
                "20201001",
                listOf("diamond", "shiny", "jewel", "gem", "hard", "rock", "crystal", "minecraft")
            ),
            Emoji(listOf(128139), "20201001", listOf("kiss", "love", "lips", "mark")),
            Emoji(listOf(129766), "20220203", listOf("lips", "biting", "nervous", "flirting", "love")),
            Emoji(listOf(128148), "20201001", listOf("heart", "broken", "love", "crack")),
            Emoji(listOf(128140), "20201001", listOf("heart", "letter", "paper", "mail", "post", "message", "love")),
            Emoji(listOf(128152), "20201001", listOf("heart", "struck", "arrow", "love", "pink")),
            Emoji(listOf(128159), "20201001", listOf("heart", "box", "purple", "love")),
            Emoji(listOf(128149), "20201001", listOf("heart", "double", "love", "pink")),
            Emoji(listOf(128158), "20201001", listOf("heart", "double", "spin", "love", "pink")),
            Emoji(listOf(128147), "20201001", listOf("heart", "vibration", "station", "throb", "beat", "love", "pink")),
            Emoji(listOf(128151), "20201001", listOf("heart", "growing", "love", "pink")),
            Emoji(
                listOf(10084, 65039, 8205, 129657),
                "20210218",
                listOf("injury", "bandage", "heart", "broken", "fix", "red", "love")
            ),
            Emoji(listOf(10083, 65039), "20201001", listOf("heart", "dot", "love", "red")),
            Emoji(listOf(9829, 65039), "20201001", listOf("heart", "love", "red")),
            Emoji(listOf(10084, 65039), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "red")),
            Emoji(listOf(129505), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "orange")),
            Emoji(listOf(128155), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "yellow")),
            Emoji(listOf(128154), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "green")),
            Emoji(listOf(128153), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "blue")),
            Emoji(listOf(128156), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "purple")),
            Emoji(listOf(129294), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "brown")),
            Emoji(listOf(129293), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "white")),
            Emoji(listOf(128420), "20201001", listOf("heart", "rainbow", "colour", "color", "love", "black")),
            Emoji(listOf(128150), "20201001", listOf("heart", "love", "sparkle")),
            Emoji(listOf(128157), "20201001", listOf("heart", "love", "ribbon", "bow", "tie")),
            Emoji(listOf(127873), "20211115", listOf("gift", "present", "ribbon", "bow", "tie", "box")),
            Emoji(listOf(127895, 65039), "20201001", listOf("yellow", "ribbon")),
            Emoji(listOf(127942), "20211115", listOf("trophy", "cup", "gold", "celebrate", "gold", "win", "award")),
            Emoji(listOf(9917), "20220406", listOf("sport", "football", "ball", "soccer")),
            Emoji(listOf(129351), "20220203", listOf("first", "place", "medal", "win", "award", "gold", "one", "1st")),
            Emoji(
                listOf(129352),
                "20220203",
                listOf("second", "place", "medal", "win", "award", "silver", "one", "2nd")
            ),
            Emoji(
                listOf(129353),
                "20220203",
                listOf("third", "place", "medal", "win", "award", "bronze", "one", "3rd")
            ),
            Emoji(listOf(127941), "20220203", listOf("gold", "medal", "win", "award", "star")),
            Emoji(listOf(128240), "20201001", listOf("news", "paper")),
            Emoji(
                listOf(128172),
                "20220203",
                listOf("chat", "speech", "text", "message", "balloon", "bubble", "cartoon")
            ),
            Emoji(listOf(127911), "20210521", listOf("head", "ear", "phone")),
            Emoji(listOf(128175), "20201001", listOf("100", "percent", "%", "one", "hundred")),
            Emoji(listOf(128064), "20201001", listOf("eyes", "look", "side")),
            Emoji(listOf(128065, 65039), "20201001", listOf("eye", "look")),
            Emoji(listOf(127751), "20210831", listOf("city", "sun", "set", "building")),
            Emoji(listOf(128371, 65039), "20201001", listOf("hole", "ground", "dark", "void")),
            Emoji(listOf(129668), "20210521", listOf("magic", "wand", "star", "sparkle")),
            Emoji(listOf(128302), "20201001", listOf("magic", "crystal", "ball", "purple")),
            Emoji(listOf(128293), "20201001", listOf("fire", "burn", "hot", "heat")),
            Emoji(listOf(128165), "20220203", listOf("explosion", "fire", "bang", "collision", "crash", "impact")),
            Emoji(listOf(128081), "20201001", listOf("crown", "gold", "royal")),
            Emoji(listOf(129460), "20220203", listOf("bone", "skeleton", "osteon", "anatomy", "body")),
            Emoji(listOf(129463), "20220203", listOf("tooth", "dentist", "mouth", "anatomy", "body")),
            Emoji(
                listOf(129504),
                "20220203",
                listOf("brain", "thought", "mind", "think", "head", "organ", "anatomy", "body")
            ),
            Emoji(listOf(129729), "20220203", listOf("lungs", "breathing", "air", "organ", "anatomy", "body")),
            Emoji(listOf(129728), "20220203", listOf("heart", "blood", "pump", "anatomy", "body", "pulse")),
            Emoji(listOf(128049), "20201001", listOf("animal", "cat", "kitten", "pussy", "meowtle")),
            Emoji(listOf(129409), "20201001", listOf("animal", "big", "cat", "lion", "rawr")),
            Emoji(listOf(128047), "20220110", listOf("animal", "big", "cat", "tiger")),
            Emoji(listOf(128053), "20201001", listOf("animal", "monkey")),
            Emoji(listOf(128584), "20201001", listOf("animal", "monkey", "eye", "cover")),
            Emoji(listOf(128055), "20201001", listOf("animal", "pig")),
            Emoji(listOf(129412), "20210831", listOf("animal", "horse", "unicorn")),
            Emoji(listOf(129420), "20201001", listOf("animal", "deer", "christmas", "xmas", "rudolf")),
            Emoji(listOf(128016), "20210831", listOf("animal", "goat", "horn", "mountain")),
            Emoji(listOf(129433), "20201001", listOf("animal", "llama", "alpaca")),
            Emoji(listOf(128038), "20210831", listOf("animal", "bird", "blue", "twitter", "fly")),
            Emoji(listOf(129417), "20210831", listOf("animal", "bird", "owl", "fly", "wise", "night")),
            Emoji(listOf(128039), "20211115", listOf("animal", "bird", "penguin", "pingu", "ice", "cold")),
            Emoji(listOf(129415), "20201001", listOf("animal", "bat", "batman", "fly", "vampire", "night")),
            Emoji(
                listOf(128029),
                "20201001",
                listOf(
                    "animal",
                    "honey",
                    "insect",
                    "arachnid",
                    "bee",
                    "pollen",
                    "bzz",
                    "black",
                    "yellow",
                    "queen",
                    "fly",
                    "stinger",
                    "bitch"
                )
            ),
            Emoji(listOf(128375, 65039), "20201001", listOf("spider", "insect", "arachnid", "spooky")),
            Emoji(listOf(128034), "20201001", listOf("animal", "turtle", "god", "best", "meowtle")),
            Emoji(listOf(128025), "20201001", listOf("animal", "octopus", "squid")),
            Emoji(listOf(128060), "20201001", listOf("animal", "panda", "bear", "asian", "kungfu")),
            Emoji(listOf(128059), "20210831", listOf("animal", "brown", "bear")),
            Emoji(listOf(128040), "20201001", listOf("animal", "koala", "bear", "dumb")),
            Emoji(listOf(129445), "20201001", listOf("animal", "sloth", "slow")),
            Emoji(listOf(128048), "20201001", listOf("animal", "rabbit", "hare", "ear", "rodent")),
            Emoji(listOf(128045), "20201001", listOf("animal", "mouse", "rat", "ear", "rodent", "cheese")),
            Emoji(listOf(129428), "20201001", listOf("animal", "hedgehog", "sonic", "sanic")),
            Emoji(listOf(128054), "20211115", listOf("animal", "dog", "mikra")),
            Emoji(listOf(128041), "20211115", listOf("animal", "dog", "poodle")),
            Emoji(listOf(129437), "20211115", listOf("animal", "raccoon")),
            Emoji(listOf(128012), "20210218", listOf("animal", "snail", "slow")),
            Emoji(listOf(129410), "20210218", listOf("animal", "scorpion")),
            Emoji(listOf(128031), "20210831", listOf("animal", "fish", "swim", "sea")),
            Emoji(listOf(127757), "20201001", listOf("earth", "planet")),
            Emoji(listOf(127774), "20201001", listOf("sun", "star", "sky", "day", "light")),
            Emoji(listOf(127775), "20201001", listOf("star", "shiny", "night", "sky")),
            Emoji(listOf(11088), "20201001", listOf("star", "shiny", "night", "sky")),
            Emoji(listOf(127772), "20201001", listOf("moon", "night", "sky", "right")),
            Emoji(listOf(127771), "20201001", listOf("moon", "night", "sky", "left")),
            Emoji(listOf(128171), "20201001", listOf("star", "sparkle", "spin", "night", "sky")),
            Emoji(listOf(127752), "20201001", listOf("rain", "bow", "gay", "homosexual", "sky", "weather")),
            Emoji(listOf(127786, 65039), "20201001", listOf("tornado", "wind", "sky", "weather")),
            Emoji(listOf(9729, 65039), "20201001", listOf("cloud", "sky", "air", "weather", "rain", "fluff"))
        )
    }
}

/**
 * EmojiMix功能
 *
 * @author RyouonRitsu
 * @param emojis 包含emoji和+号的字符串
 * @return 保存的混合emoji图片地址
 */
fun emojiMix(emojis: String): String {
    val emojiCollection =
        Regex("&#\\d+;").findAll(EmojiConverter.getInstance().toHtml(emojis)).map { it.value }.toList()
    if (emojiCollection.size != 2) return "识别错误, 你不能mix${emojiCollection.size}个emoji, 可能不支持这个iOS版本的emoji, 亦或是你使用的是QQ的emoji, 此功能仅支持系统原生的emoji"
    val emoji1 = emojiCollection[0].replace("&#", "").replace(";", "").toInt()
    val emoji2 = emojiCollection[1].replace("&#", "").replace(";", "").toInt()
    val result = mixEmoji(emoji1, emoji2)
    return if (result.startsWith("http")) {
        val (code, msg) = download(result, "./data/Image/${emoji1 + emoji2}.png")
        if (code == 200 && msg == null) "./data/Image/${emoji1 + emoji2}.png" else msg!!
    } else result
}

/**
 * 根据emoji的信息生成对应的图片地址
 *
 * @author RyouonRitsu
 * @param emoji1 第一个emoji
 * @param emoji2 第二个emoji
 * @return 混合后的图片地址
 */
fun createUrl(emoji1: Emoji, emoji2: Emoji): String {
    fun emojiCode(emoji: Emoji): String {
        return emoji.code.joinToString("-") { String.format("u%x", it) }
    }

    val u1 = emojiCode(emoji1)
    val u2 = emojiCode(emoji2)
    return "${Emoji.API}${emoji1.str}/${u1}/${u1}_${u2}.png"
}

/**
 * 根据emoji的十进制编码在支持的Emoji集合中查找对应的Emoji
 *
 * @author RyouonRitsu
 * @param emojiNum emoji的十进制编码
 * @return 对应的Emoji实例
 */
fun findEmoji(emojiNum: Int): Emoji? {
    for (e in Emoji.emojis) {
        if (emojiNum in e.code) {
            return e
        }
    }
    return null
}

/**
 * 尝试混合两个emoji，给出结果
 *
 * @author RyouonRitsu
 * @param emojiNum1 第一个emoji的十进制编码
 * @param emojiNum2 第二个emoji的十进制编码
 * @return 混合结果
 */
fun mixEmoji(emojiNum1: Int, emojiNum2: Int): String {
    val emoji1 = findEmoji(emojiNum1)
    val emoji2 = findEmoji(emojiNum2)
    if (emoji1 == null) return "不支持的emoji: ${EmojiConverter.getInstance().toUnicode("&#$emojiNum1;")}"
    if (emoji2 == null) return "不支持的emoji: ${EmojiConverter.getInstance().toUnicode("&#$emojiNum2;")}"

    val url1 = createUrl(emoji1, emoji2)
    val url2 = createUrl(emoji2, emoji1)

    var url = URL(url1)
    var con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    con.doOutput = true
    con.doInput = true
    con.useCaches = false
    return if (con.responseCode == HttpURLConnection.HTTP_OK) {
        url1
    } else {
        url = URL(url2)
        con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.doOutput = true
        con.doInput = true
        con.useCaches = false
        if (con.responseCode == HttpURLConnection.HTTP_OK) {
            url2
        } else "出错了, 暂不支持这种emoji组合"
    }
}