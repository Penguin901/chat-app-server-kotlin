package com.example.chatapp

import com.example.chatapp.chat.room.ChatRoomUseCase
import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CreateChatroomTest(
    private val chatRoomUseCase: ChatRoomUseCase,
) {
    @Test
    fun `create one chat room for two users when requested concurrently`() {
        val executor = Executors.newFixedThreadPool(2)
        val ready = CountDownLatch(2)
        val start = CountDownLatch(1)

        val thread1 = executor.submit<CreateChatRoomResponse> {
            ready.countDown()
            start.await(5, TimeUnit.SECONDS)

            chatRoomUseCase.getOrCreateChatRoom(
                405,
                createChatRoomRequest = CreateChatRoomRequest(null, listOf(406))
            )
        }

        val thread2 = executor.submit<CreateChatRoomResponse> {
            ready.countDown()
            start.await(5, TimeUnit.SECONDS)

            chatRoomUseCase.getOrCreateChatRoom(
                406,
                createChatRoomRequest = CreateChatRoomRequest(null, listOf(405))
            )
        }

        ready.await(5, TimeUnit.SECONDS)
        start.countDown()

        val room1 = thread1.get()
        val room2 = thread2.get()

        assertEquals(room1.id, room2.id)
        executor.shutdown()
    }
}