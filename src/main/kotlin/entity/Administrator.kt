package org.ritsu.mirai.plugin.entity

import net.mamoe.mirai.contact.Member

data class Administrator(val account: Member) {
    companion object {
        val administrators: List<Long> = listOf(
            1780645196L,
        )
    }
}
