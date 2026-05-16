package com.example.chatapp.friend

import com.example.chatapp.friend.dto.AddFriendRequest
import com.example.chatapp.friend.dto.SearchFriendCandidateResponse
import com.example.chatapp.friend.dto.SearchType
import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.user.dto.response.UserProfileResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/friends")
@Validated
class FriendController(
    private val friendService: FriendService,
    private val friendUseCase: FriendUseCase
) {
    @GetMapping
    fun getMyFriends(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): List<UserProfileResponse> {
        return friendService.getFriends(userPrincipal.userId)
    }

    // 친구로 등록할 사용자 검색
    @GetMapping("/search")
    fun searchFriendCandidate(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam searchType: SearchType,
        @RequestParam @NotBlank keyword: String
    ): SearchFriendCandidateResponse {
        return friendUseCase.searchFriendCandidate(
            userPrincipal.userId,
            searchType,
            keyword
        )
    }

    @PostMapping
    fun addFriend(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: AddFriendRequest
    ): ResponseEntity<UserProfileResponse> {
        val friendProfile = friendUseCase.addFriend(
            userPrincipal.userId,
            request.targetUserId
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(friendProfile)
    }

    @DeleteMapping("/{targetUserId}")
    fun removeFriend(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable targetUserId: Long
    ): ResponseEntity<Void> {
        friendService.removeFriend(userPrincipal.userId, targetUserId)
        return ResponseEntity.noContent().build()
    }
}