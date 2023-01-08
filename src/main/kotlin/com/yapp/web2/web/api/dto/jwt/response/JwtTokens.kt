package com.yapp.web2.web.api.dto.jwt.response

data class JwtTokens(
    val accessToken: String? = null,
    val refreshToken: String? = null
)
