package com.yapp.web2.domain.jwt.application.oauth

interface AuthService {
    fun requestToken(authCode: String): String
    fun getUserEmail(token: String): String
}
