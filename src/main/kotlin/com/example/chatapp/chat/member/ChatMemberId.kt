package com.example.chatapp.chat.member

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.Objects

@Embeddable
class ChatMemberId(
    val chatRoomId: Long? = null,
    val userId: Long? = null,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatMemberId) return false

        return chatRoomId == other.chatRoomId && userId == other.userId
    }

    override fun hashCode(): Int = Objects.hash(chatRoomId, userId)
}
