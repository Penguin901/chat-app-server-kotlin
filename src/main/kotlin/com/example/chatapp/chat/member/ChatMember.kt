package com.example.chatapp.chat.member

import com.example.chatapp.chat.room.ChatRoom
import com.example.chatapp.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 특정 채팅방에 참여한 사용자와 채팅방 간의 관계를 나타냅니다.
 *
 * activate 필드를 통해 입장 및 퇴장 상태를 관리하며,
 * (chat_room_id, user_id) 복합 유니크 제약 조건을 통해 동일한 사용자가 같은 채팅방에 중복 참여하는 것을 방지합니다.
 */
@Entity
@Table(
    name = "chat_members",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_chat_members_chat_room_id_user_id", columnNames = ["chat_room_id", "user_id"]
    )],
    indexes = [Index(name = "idx_chat_members_user_id_active", columnList = "user_id, active")]
)
class ChatMember private constructor(
    chatRoom: ChatRoom,
    user: User,
    createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @EmbeddedId
    val id: ChatMemberId = ChatMemberId()

    @MapsId("chatRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    var chatRoom: ChatRoom = chatRoom
        private set

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        private set

    @Column(nullable = false)
    var active = true
        private set

    @Column(nullable = false)
    var joinedAt: LocalDateTime = LocalDateTime.now()
        private set

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = createdAt
        private set

    fun activate() {
        if (!this.active) {
            this.active = true
            this.joinedAt = LocalDateTime.now()
        }
    }

    fun deactivate() {
        if (this.active)
            this.active = false
    }

    companion object {
        fun create(chatRoom: ChatRoom, user: User): ChatMember {
            return ChatMember(chatRoom, user)
        }
    }
}