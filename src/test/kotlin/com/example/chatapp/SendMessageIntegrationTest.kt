package com.example.chatapp

import com.example.chatapp.auth.oauth.OAuthProvider
import com.example.chatapp.chat.message.ChatMessageStompController
import com.example.chatapp.chat.message.ChatMessageRepository
import com.example.chatapp.chat.message.dto.stomp.SendChatMessage
import com.example.chatapp.chat.message.dto.stomp.ChatMessageEvent
import com.example.chatapp.chat.room.ChatRoomUseCase
import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse
import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.user.User
import com.example.chatapp.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class SendMessageIntegrationTest(
    private val userRepository: UserRepository,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatMessageStompController: ChatMessageStompController,
) {
    @MockBean
    lateinit var simpMessagingTemplate: SimpMessagingTemplate

    @Test
    fun `save and broadcast a message`() {
        val sender = User.create("sender@test.com", "sender-oauth-id", OAuthProvider.GOOGLE)
        sender.updateUserProfile("sender", "", "")
        userRepository.save(sender)

        val receiver = User.create("receiver@test.com", "receiver-oauth-id", OAuthProvider.GOOGLE)
        receiver.updateUserProfile("receiver", "", "")
        userRepository.save(receiver)

        val chatRoomCreateRequest = CreateChatRoomRequest(roomName = "", participantIds = listOf(receiver.id!!))
        val createChatRoomResponse: CreateChatRoomResponse =
            chatRoomUseCase.getOrCreateChatRoom(sender.id!!, chatRoomCreateRequest)

        val sendChatMessage = SendChatMessage(createChatRoomResponse.id, "test 메세지")

        val authorities = listOf(SimpleGrantedAuthority("USER_EDIT"))
        val userPrincipal = UserPrincipal(sender.id!!, authorities)
        val authentication = UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.authorities)

        chatMessageStompController.sendMessage(authentication, sendChatMessage)
        val messages = chatMessageRepository.findAll()

        assertThat(messages).hasSize(1)
        assertThat(messages[0].messageContent).isEqualTo("test 메세지")

        val chatMessageEventArgumentCaptor = ArgumentCaptor.forClass(ChatMessageEvent::class.java)

        Mockito.verify(simpMessagingTemplate).convertAndSend(
            ArgumentMatchers.eq("/topic/chatroom/" + createChatRoomResponse.id),
            chatMessageEventArgumentCaptor.capture()
        )

        val sendMessageResponseCaptorValue = chatMessageEventArgumentCaptor.value

        assertThat(sendMessageResponseCaptorValue.sender.userId).isEqualTo(sender.id)
        assertThat(sendMessageResponseCaptorValue.chatRoomId).isEqualTo(createChatRoomResponse.id)
        assertThat(sendMessageResponseCaptorValue.messageContent).isEqualTo(messages[0].messageContent)
    }
}