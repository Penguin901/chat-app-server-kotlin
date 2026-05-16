package com.example.chatapp.user.dto.response

import com.example.chatapp.user.User

data class UserProfileResponse(
    val id: Long,
    val nickname: String,
    val bio: String?,
    val profileImageUrl: String?
) {
    companion object {
        fun from(user: User): UserProfileResponse {
            return UserProfileResponse(
                user.id!!,
                user.nickname!!,
                user.bio,
                user.profileImageUrl
            )
        }
    }
}