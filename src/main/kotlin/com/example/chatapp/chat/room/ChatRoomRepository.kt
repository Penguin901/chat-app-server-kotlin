package com.example.chatapp.chat.room

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    @Query(
        """
        SELECT cm.chatRoom
        FROM ChatMember cm
        WHERE cm.user.id = :userId
          AND cm.active = TRUE
    
    """
    )
    fun findActiveChatRoomsByUserId(userId: Long): List<ChatRoom>

    fun findByDirectRoomKey(directRoomKey: String): ChatRoom?
}