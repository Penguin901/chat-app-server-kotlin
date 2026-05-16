package com.example.chatapp.chat.room

import com.example.chatapp.chat.message.ChatMessage
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 사용자들의 대화 정보를 저장합니다.
 *
 * DIRECT 타입(1대1채팅)의 경우 direct_room_key 유니크 제약 조건을 통해 동일한 사용자 간 중복 채팅방 생성을 방지합니다.
 * 마지막 메세지 정보(last_message_content, last_message_at)를 저장하여 메세지 테이블을 조회하지 않고 채팅방 목록을 정렬합니다.
 */
@Entity
@Table(
    name = "chat_rooms",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_chat_rooms_direct_room_key", columnNames = ["direct_room_key"]
        )
    ]
)
class ChatRoom private constructor(
    roomType: RoomType,
    roomName: String?,
    directRoomKey: String?,
    createdAt: LocalDateTime = LocalDateTime.now(),
) {
    enum class RoomType {
        DIRECT,
        GROUP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    var roomType: RoomType = roomType
        private set

    @Column(length = 100)
    var roomName: String? = roomName
        private set

    var directRoomKey: String? = directRoomKey
        private set

    @Column(nullable = false)
    var lastMessageContent: String = ""
        private set

    @Column(nullable = false)
    var lastMessageAt: LocalDateTime = LocalDateTime.now()
        private set

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = createdAt
        private set

    fun updateLastMessageContent(message: ChatMessage) {
        this.lastMessageContent = message.messageContent
        this.lastMessageAt = message.sentAt
    }

    companion object {

        fun createDirect(directRoomKey: String): ChatRoom {
            return ChatRoom(
                roomType = RoomType.DIRECT,
                roomName = null,
                directRoomKey = directRoomKey
            )
        }

        fun createGroup(roomName: String?): ChatRoom {
            return ChatRoom(
                roomType = RoomType.GROUP,
                roomName = roomName,
                directRoomKey = null
            )
        }
    }
}