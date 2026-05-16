package com.example.chatapp.friend

import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.common.exception.FriendException
import com.example.chatapp.user.User
import com.example.chatapp.user.dto.response.UserProfileResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendServiceImpl(
    private val friendRepository: FriendRepository
) : FriendService {

    override fun getFriends(currentUserId: Long): List<UserProfileResponse> {
        val friends = friendRepository.findByUserId(currentUserId)

        return friends.map { friend ->
            UserProfileResponse.from(friend.friendUser)
        }
    }

    override fun createFriendRelation(currentUser: User, targetUser: User): User {
        val currentUserId = currentUser.id!!
        val targetUserId = targetUser.id!!

        if (friendRepository.existsByUserIdAndFriendUserId(currentUserId, targetUserId)) { //친구관계가 존재할 때
            throw FriendException(ErrorCode.FRIEND_ALREADY_EXISTS)
        }

        val friend = Friend.create(currentUser, targetUser)
        friendRepository.save(friend)

        return targetUser
    }

    @Transactional
    override fun removeFriend(currentUserId: Long, targetUserId: Long) {
        friendRepository.deleteByUserIdAndFriendUserId(currentUserId, targetUserId)
    }
}