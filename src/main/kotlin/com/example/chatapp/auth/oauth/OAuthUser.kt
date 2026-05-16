package com.example.chatapp.auth.oauth

interface OAuthUser {
    val oauthId: String
    val provider: OAuthProvider
    val email: String
    val isEmailVerified: Boolean
}