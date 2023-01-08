package com.yapp.web2.domain.jwt.application.oauth

import org.springframework.http.ResponseEntity

interface AuthService {
    fun requestToken(authCode: String): ResponseEntity<String>
    fun getUserEmail(token: String): String
}
