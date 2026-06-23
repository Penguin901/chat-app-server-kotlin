package com.example.chatapp

import com.example.chatapp.auth.oauth.OAuthProvider
import com.example.chatapp.chat.member.ChatMemberRepository
import com.example.chatapp.chat.message.ChatMessageStompController
import com.example.chatapp.chat.message.dto.stomp.SendChatMessage
import com.example.chatapp.chat.room.ChatRoomUseCase
import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse
import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.user.User
import com.example.chatapp.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ReactivateChatMemberIntegrationTest(
    private val userRepository: UserRepository,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val chatMemberRepository: ChatMemberRepository,
    private val chatMessageStompController: ChatMessageStompController
) {
    @Test
    fun `reactivate a chat member when another member sends a message`() {
        val sender = User.create("sender@test.com", "sender-oauth-id", OAuthProvider.GOOGLE)
        sender.updateUserProfile("sender", "", "")
        userRepository.save(sender)

        val receiver = User.create("receiver@test.com", "receiver-oauth-id", OAuthProvider.GOOGLE)
        receiver.updateUserProfile("receiver", "", "")
        userRepository.save(receiver)

        // 채팅방 생성 -> 모든멤버 활성화 (sender의 메세지전송은 생략함)
        val chatRoomCreateRequest = CreateChatRoomRequest(roomName = "", participantIds = listOf(receiver.id!!))
        val createChatRoomResponse: CreateChatRoomResponse =
            chatRoomUseCase.getOrCreateChatRoom(sender.id!!, chatRoomCreateRequest)

        // sender 채팅방 퇴장 -> 비활성멤버로 업데이트
        chatRoomUseCase.leaveChatRoom(sender.id!!, createChatRoomResponse.id)

        val inactiveSender = chatMemberRepository.findByChatRoomIdAndUserId(createChatRoomResponse.id, sender.id!!)

        assertThat(inactiveSender).isNotNull()
        assertThat(inactiveSender!!.active).isFalse()

        val sendChatMessage = SendChatMessage(createChatRoomResponse.id, "test 메세지")

        val authorities = listOf(SimpleGrantedAuthority("USER_EDIT"))
        val userPrincipal = UserPrincipal(receiver.id!!, authorities)
        val authentication = UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.authorities)

        // receiver가 sender에게 메세지 전송 -> 비활성멤버의 경우 활성멤버로 업데이트
        chatMessageStompController.sendMessage(authentication, sendChatMessage)

        val reactivatedSender = chatMemberRepository.findByChatRoomIdAndUserId(createChatRoomResponse.id, sender.id!!)

        assertThat(reactivatedSender).isNotNull()
        assertThat(reactivatedSender!!.active).isTrue()
    }

}