package com.yapp.web2.web.api.dto.member.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.web2.web.api.dto.jwt.response.JwtTokens

data class SignIn(
    @get:JsonProperty("isMember")
    @param:JsonProperty("isMember")
    val isMember: Boolean,
    val jwtTokens: JwtTokens
)
