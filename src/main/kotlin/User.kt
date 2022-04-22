package org.ritsu.mirai.plugin

import net.mamoe.mirai.contact.Member
import java.text.SimpleDateFormat
import java.util.*

class User(val account: Member) {
    companion object {
        private val users = HashMap<Long, User>()

        fun getUser(account: Member): User {
            return users.getOrPut(account.id) { User(account) }
        }
    }

    var luckyValue: Int = -1
    var luckyValueAcquisitionDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
}
