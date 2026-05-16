package com.example.chatapp.friend

import com.example.chatapp.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 사용자간 친구 관계를 나타냅니다.
 *
 * (user_id, friend_user_id) 복합 유니크 제약 조건을 통해 동일한 친구 관계가 중복으로 생성되는 것을 방지합니다.
 */
@Entity
@Table(
    name = "friends",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_user_id_friend_user_id", columnNames = ["user_id", "friend_user_id"]
    )]
)

class Friend private constructor(
    user: User,
    friendUser: User,
    createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id", nullable = false)
    var friendUser: User = friendUser
        private set

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = createdAt
        private set

    companion object {
        fun create(user: User, friendUser: User): Friend {
            /*  서비스에서 검증하는데 또 할지 고민
              if (user.id == friendUser.id) {
                   throw FriendException(ErrorCode.SELF_FRIEND_NOT_ALLOWED)
               } */
            return Friend(user, friendUser)
        }
    }
}
