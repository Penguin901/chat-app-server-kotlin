package com.example.chatapp.user

import com.example.chatapp.auth.domain.RefreshTokenInfo
import com.example.chatapp.auth.oauth.OAuthUser
import com.example.chatapp.common.exception.AuthException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.common.exception.UserException
import com.example.chatapp.friend.dto.SearchType
import com.example.chatapp.user.dto.request.UpdateUsernameRequest
import com.example.chatapp.user.dto.request.UpdateUserProfileRequest
import com.example.chatapp.user.dto.response.UpdateUsernameResponse
import com.example.chatapp.user.dto.response.UserProfileResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getUserOrThrow(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw UserException(ErrorCode.USER_NOT_FOUND)
    }

    override fun getUsersOrThrow(userIds: List<Long>): List<User> {
        val users = userRepository.findAllById(userIds)

        if (users.size != userIds.size) {
            throw UserException(ErrorCode.USER_NOT_FOUND)
        }

        return users
    }

    override fun findOrCreateUserByOAuth(oAuthUser: OAuthUser): User {
        val provider = oAuthUser.provider
        val existingUser = userRepository.findByOauthProviderAndOauthId(oAuthUser.provider, oAuthUser.oauthId)

        if (existingUser != null) {
            // 탈퇴한 회원인 경우
            if (existingUser.deleted) {
                existingUser.reactivateAccount(provider, oAuthUser.oauthId)
                return existingUser
            }

            if (existingUser.oauthProvider != provider) {
                throw AuthException(ErrorCode.OAUTH_PROVIDER_MISMATCH)
            }

            return existingUser
        }

        // 사용자가 존재하지 않는 경우 새로운 사용자 생성
        val newUser = User.create(
            oAuthUser.email,
            oAuthUser.oauthId,
            provider
        )

        return userRepository.save(newUser)
    }

    @Transactional
    override fun updateRefreshToken(currentUserId: Long, refreshTokenInfo: RefreshTokenInfo) {
        val user = getUserOrThrow(currentUserId)

        // RefreshTokenInfo에서 유효한 값인지 이미 검증함
        user.updateTokenInfo(refreshTokenInfo)
    }

    @Transactional
    override fun updateUserProfile(
        currentUserId: Long,
        request: UpdateUserProfileRequest
    ): UserProfileResponse {
        val user = getUserOrThrow(currentUserId)

        user.updateUserProfile(
            request.nickname,
            request.bio,
            request.profileImageUrl
        )

        return UserProfileResponse.from(user)
    }

    @Transactional
    override fun updateUsername(
        currentUserId: Long,
        request: UpdateUsernameRequest
    ): UpdateUsernameResponse {
        val trimmedUsername = request.username.trim()

        if (!isUsernameAvailable(currentUserId, trimmedUsername)) {
            throw UserException(ErrorCode.USERNAME_ALREADY_EXISTS)
        }

        val user = getUserOrThrow(currentUserId)
        user.updateUsername(trimmedUsername)

        return UpdateUsernameResponse(trimmedUsername)
    }

    override fun searchUserExcludingSelf(
        currentUserId: Long,
        searchType: SearchType,
        keyword: String
    ): User? {
        val trimmedKeyword = keyword.trim().lowercase()

        return when (searchType) {
            SearchType.EMAIL ->
                userRepository.findByEmailAndDeletedFalseAndIdNot(trimmedKeyword, currentUserId)

            SearchType.USERNAME ->
                userRepository.findByUsernameAndDeletedFalseAndIdNot(trimmedKeyword, currentUserId)
        }
    }

    override fun getUserProfile(userId: Long): UserProfileResponse {
        val user = getUserOrThrow(userId)
        return UserProfileResponse.from(user)
    }

    override fun isUsernameAvailable(currentUserId: Long, username: String): Boolean {
        val trimmedUsername = username.trim()
        validateUsername(trimmedUsername)
        return !userRepository.existsByUsernameAndIdNot(trimmedUsername, currentUserId)
    }

    @Transactional
    override fun deleteAccount(currentUserId: Long) {
        val user = getUserOrThrow(currentUserId)
        user.deleteAccount()
    }

    private fun validateUsername(trimmedUsername: String) {
        if (trimmedUsername.length < 4) {
            throw UserException(ErrorCode.INVALID_USERNAME)
        }

        if (trimmedUsername.any { it.isWhitespace() }) {
            throw UserException(ErrorCode.INVALID_USERNAME)
        }
    }
}