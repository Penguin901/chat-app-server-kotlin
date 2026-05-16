package com.example.chatapp.friend.dto

import com.example.chatapp.user.User
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SearchFriendCandidateResponse(
    val found: Boolean,
    val alreadyFriend: Boolean,
    val userId: Long?,
    val nickname: String?,
    val bio: String?,
    val profileImageUrl: String?
) {
    companion object {
        fun notFound(): SearchFriendCandidateResponse {
            return SearchFriendCandidateResponse(
                found = false,
                alreadyFriend = false,
                userId = null,
                nickname = null,
                bio = null,
                profileImageUrl = null
            )
        }

        fun found(
            alreadyFriend: Boolean,
            targetUser: User
        ): SearchFriendCandidateResponse {
            return SearchFriendCandidateResponse(
                found = true,
                alreadyFriend = alreadyFriend,
                userId = targetUser.id!!,
                nickname = targetUser.nickname!!,
                bio = targetUser.bio,
                profileImageUrl = targetUser.profileImageUrl
            )
        }
    }
}