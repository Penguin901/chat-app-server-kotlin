package com.example.chatapp.friend

import com.example.chatapp.user.User
import com.example.chatapp.user.dto.response.UserProfileResponse

interface FriendService {

    fun getFriends(currentUserId: Long): List<UserProfileResponse>

    fun createFriendRelation(currentUser: User, targetUser: User): User // 유저아이디만 받을지 고민

    fun removeFriend(currentUserId: Long, targetUserId: Long)
}