package com.example.chatapp.user

import com.example.chatapp.auth.domain.RefreshTokenInfo
import com.example.chatapp.auth.oauth.OAuthUser
import com.example.chatapp.friend.dto.SearchType
import com.example.chatapp.user.dto.request.UpdateUsernameRequest
import com.example.chatapp.user.dto.request.UpdateUserProfileRequest
import com.example.chatapp.user.dto.response.UpdateUsernameResponse
import com.example.chatapp.user.dto.response.UserProfileResponse

interface UserService {

    fun getUserOrThrow(userId: Long): User

    fun getUsersOrThrow(userIds: List<Long>): List<User>

    fun findOrCreateUserByOAuth(oAuthUser: OAuthUser): User

    fun updateRefreshToken(currentUserId: Long, refreshTokenInfo: RefreshTokenInfo)

    fun updateUserProfile(currentUserId: Long, request: UpdateUserProfileRequest): UserProfileResponse

    fun updateUsername(currentUserId: Long, request: UpdateUsernameRequest): UpdateUsernameResponse

    fun searchUserExcludingSelf(currentUserId: Long, searchType: SearchType, keyword: String): User?

    fun getUserProfile(userId: Long): UserProfileResponse

    fun isUsernameAvailable(currentUserId: Long, username: String): Boolean

    fun deleteAccount(currentUserId: Long)
}