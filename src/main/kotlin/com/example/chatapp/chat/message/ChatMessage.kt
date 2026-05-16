package com.example.chatapp.chat.message

import com.example.chatapp.chat.room.ChatRoom
import com.example.chatapp.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 특정 채팅방에 속한 메세지를 저장합니다.
 *
 * (chat_room_id, sent_at) 복합 인덱스로 최근 메세지부터 조회합니다.
 */
@Entity
@Table(
    name = "chat_messages",
    indexes = [
        Index(name = "idx_chat_room_sent_at", columnList = "chat_room_id, sent_at")
    ]
)
class ChatMessage private constructor(
    chatRoom: ChatRoom,
    sender: User,
    messageContent: String,
    sentAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    var chatRoom: ChatRoom = chatRoom
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User = sender
        private set

    @Column(nullable = false)
    var messageContent: String = messageContent
        private set

    @Column(nullable = false, updatable = false)
    var sentAt: LocalDateTime = sentAt
        private set

    companion object {
        fun create(chatRoom: ChatRoom, sender: User, messageContent: String): ChatMessage {
            return ChatMessage(chatRoom, sender, messageContent)
        }
    }
}