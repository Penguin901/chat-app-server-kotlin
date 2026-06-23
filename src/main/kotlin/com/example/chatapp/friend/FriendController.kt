package com.example.chatapp.friend

import com.example.chatapp.friend.dto.AddFriendRequest
import com.example.chatapp.friend.dto.SearchFriendCandidateResponse
import com.example.chatapp.friend.dto.SearchType
import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.user.dto.response.UserProfileResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Friend", description = "사용자 간 친구관계 API")
class FriendController(
    private val friendService: FriendService,
    private val friendUseCase: FriendUseCase
) {
    @Operation(summary = "친구 목록 조회", description = "현재 로그인한 사용자의 친구 목록을 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @GetMapping
    fun getMyFriends(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): List<UserProfileResponse> {
        return friendService.getFriends(userPrincipal.userId)
    }

    @Operation(summary = "친구로 등록할 사용자 조회", description = "친구로 추가할 사용자의 존재 및 사용자 간 친구 관계 여부를 확인합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
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

    @Operation(summary = "친구 추가", description = "현재 로그인한 사용자와 다른 사용자(targetUserId)의 친구관계를 추가합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
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

    @Operation(summary = "친구 삭제", description = "현재 로그인한 사용자와 다른 사용자(targetUserId)의 친구관계를 삭제합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패")
    )
    @DeleteMapping("/{targetUserId}")
    fun removeFriend(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable targetUserId: Long
    ): ResponseEntity<Void> {
        friendService.removeFriend(userPrincipal.userId, targetUserId)
        return ResponseEntity.noContent().build()
    }
}