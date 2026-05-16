package com.example.chatapp.chat.message.dto.response

import com.example.chatapp.user.User

data class SenderInfo(
    val userId: Long,
    val nickname: String,
    val bio: String?,
    val profileImageUrl: String?
) {
    companion object {
        fun from(user: User): SenderInfo {
            return SenderInfo(
                userId = requireNotNull(user.id),
                nickname = requireNotNull(user.nickname),
                user.bio,
                user.profileImageUrl
            )
        }
    }
}