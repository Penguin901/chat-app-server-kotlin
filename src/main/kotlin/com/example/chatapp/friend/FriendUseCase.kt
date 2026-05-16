package com.example.chatapp.friend

import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.common.exception.FriendException
import com.example.chatapp.common.exception.UserException
import com.example.chatapp.friend.dto.SearchFriendCandidateResponse
import com.example.chatapp.friend.dto.SearchType
import com.example.chatapp.user.User
import com.example.chatapp.user.UserService
import com.example.chatapp.user.dto.response.UserProfileResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendUseCase(
    private val userService: UserService,
    private val friendService: FriendService,
    private val friendRepository: FriendRepository
) {
    fun searchFriendCandidate(
        currentUserId: Long,
        searchType: SearchType,
        keyword: String
    ): SearchFriendCandidateResponse {
        if (keyword.trim().length < 4) {
            throw UserException(ErrorCode.INVALID_SEARCH_KEYWORD)
        }

        val targetUser = userService.searchUserExcludingSelf(currentUserId, searchType, keyword)
            ?: return SearchFriendCandidateResponse.notFound()

        val targetUserId = targetUser.id!!
        val alreadyFriend = friendRepository.existsByUserIdAndFriendUserId(currentUserId, targetUserId)

        return SearchFriendCandidateResponse.found(
            alreadyFriend = alreadyFriend,
            targetUser
        )
    }

    @Transactional
    fun addFriend(currentUserId: Long, targetUserId: Long): UserProfileResponse {
        if (currentUserId == targetUserId) {
            throw FriendException(ErrorCode.SELF_FRIEND_NOT_ALLOWED)
        }

        val users: List<User> = userService.getUsersOrThrow(listOf(currentUserId, targetUserId))
        val userMap = users.associateBy { user -> user.id }

        val currentUser = userMap[currentUserId]!!
        val targetUser = userMap[targetUserId]!!

        val friendUser = friendService.createFriendRelation(currentUser, targetUser)

        return UserProfileResponse.from(friendUser)
    }
}