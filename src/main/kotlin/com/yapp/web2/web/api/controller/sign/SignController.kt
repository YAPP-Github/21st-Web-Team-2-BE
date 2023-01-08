package com.yapp.web2.web.api.controller.sign

import com.yapp.web2.domain.member.application.SignService
import com.yapp.web2.web.api.dto.jwt.response.JwtTokens
import com.yapp.web2.web.api.dto.member.response.SignIn
import com.yapp.web2.web.api.dto.sign.request.SignUpDto
import com.yapp.web2.web.api.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class SignController(
    private val signService: SignService
) {
    @PostMapping("/signin")
    fun signIn(@RequestHeader(value = "oauth-token") token: String): ApiResponse<SignIn> {
        return ApiResponse.success(signService.signIn(token))
    }

    @PostMapping("/signup")
    fun signup(@RequestHeader(value = "oauth-token") token: String,
               @RequestBody signUpDto: SignUpDto): ApiResponse<JwtTokens> {
        return ApiResponse.success(signService.signup(token, signUpDto))
    }
}
