package com.example.chatapp.chat.message

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {

    @Query(
        value = """
       select msg
       from ChatMessage msg
       where msg.chatRoom.id = :chatRoomId
         and msg.sentAt >= :joinedAt
       order by msg.sentAt desc
   
   """
    )
    fun findByChatRoomIdAndSentAtAfter(chatRoomId: Long, joinedAt: LocalDateTime): List<ChatMessage>

    @Query(
        """
    SELECT cm
    FROM ChatMessage cm
    WHERE cm.id IN (
        SELECT MAX(cm2.id)
        FROM ChatMessage cm2
        WHERE cm2.chatRoom.id IN :roomIds
        GROUP BY cm2.chatRoom.id
    )
    """
    )
    fun findLatestMessagesByChatRoomIds(
        @Param("roomIds") roomIds: List<Long>
    ): List<ChatMessage>

    fun deleteByChatRoomId(chatRoomId: Long)
}