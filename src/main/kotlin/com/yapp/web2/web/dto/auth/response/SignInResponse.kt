package com.yapp.web2.web.dto.auth.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.web2.web.dto.jwt.response.JwtTokens

data class SignInResponse(
    @get:JsonProperty("isMember")
    @param:JsonProperty("isMember")
    val isMember: Boolean,
    val jwtTokens: JwtTokens
)
