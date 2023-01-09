package com.yapp.web2.domain.jwt.application.oauth

interface OAuthService {
    fun requestToken(authCode: String): String
    fun getUserEmail(token: String): String
}
