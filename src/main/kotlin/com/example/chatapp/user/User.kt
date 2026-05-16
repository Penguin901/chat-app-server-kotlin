package com.example.chatapp.user

import com.example.chatapp.auth.domain.RefreshTokenInfo
import com.example.chatapp.auth.oauth.OAuthProvider
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * OAuth 로그인을 통해 생성된 사용자 계정 및 프로필 정보를 저장합니다.
 *
 * Refresh Token을 통해 사용자의 인증정보를 관리합니다.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_email", columnNames = ["email"]),
        UniqueConstraint(name = "uk_user_username", columnNames = ["username"])
    ]
)

class User private constructor(
    email: String,
    oauthId: String,
    oauthProvider: OAuthProvider,
    createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    // 계정 식별 정보 -> 이메일 검색 없애기
    @Column(length = 100, nullable = false)
    var email: String = email
        private set

    @Column(nullable = false)
    var oauthId: String = oauthId
        private set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var oauthProvider: OAuthProvider = oauthProvider
        private set

    // 공개용 식별 정보
    @Column(length = 20)
    var username: String? = null
        private set

    // 프로필 정보
    @Column(length = 20)
    var nickname: String? = null //빈 문자열로 초기화. 이후 이름 설정 화면에서 값 저장
        private set

    var bio: String? = null //상태메세지
        private set

    var profileImageUrl: String? = null
        private set

    var refreshToken: String? = null
        private set

    var refreshExpiration: LocalDateTime? = null
        private set

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = createdAt
        private set

    @Column(nullable = false)
    var deleted = false
        private set

    var deletedAt: LocalDateTime? = null
        private set

    // 앞 뒤 공백제거
    fun updateUserProfile(newNickname: String, bio: String?, profileImageUrl: String?) {
        this.nickname = newNickname
        this.bio = bio
        this.profileImageUrl = profileImageUrl
    }

    fun updateUsername(username: String) {
        this.username = username
    }

    fun updateTokenInfo(refreshTokenInfo: RefreshTokenInfo) {
        this.refreshToken = refreshTokenInfo.refreshToken
        this.refreshExpiration = refreshTokenInfo.refreshExpiration
    }

    fun reactivateAccount(provider: OAuthProvider, oauthId: String) {
        this.deleted = false
        this.deletedAt = null
        this.oauthProvider = provider
        this.oauthId = oauthId
    }

    // 현재는 계정 비활성화하는 것으로 탈퇴 처리
    fun deleteAccount() {
        this.deleted = true
        this.deletedAt = LocalDateTime.now()
    }

    companion object {
        fun create(email: String, oauthId: String, provider: OAuthProvider): User {
            return User(email, oauthId, provider)
        }
    }
}