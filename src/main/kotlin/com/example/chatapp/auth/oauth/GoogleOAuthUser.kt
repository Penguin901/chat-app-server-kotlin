package com.example.chatapp.auth.oauth

data class GoogleOAuthUser(
    override val oauthId: String,
    override val email: String,
    override val isEmailVerified: Boolean,
    override val provider: OAuthProvider = OAuthProvider.GOOGLE
) : OAuthUser