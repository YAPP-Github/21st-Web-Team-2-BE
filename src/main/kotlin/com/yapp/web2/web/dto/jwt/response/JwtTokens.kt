package com.yapp.web2.web.dto.jwt.response

data class JwtTokens(
    val accessToken: String? = null,
    val refreshToken: String? = null
)
