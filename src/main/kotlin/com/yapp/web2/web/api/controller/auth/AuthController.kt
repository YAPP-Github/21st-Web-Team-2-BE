package com.yapp.web2.web.api.controller.auth

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.domain.jwt.application.AuthService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.dto.auth.response.SignInResponse
import com.yapp.web2.web.dto.auth.request.SignUpRequest
import com.yapp.web2.web.api.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signin")
    fun signIn(@RequestHeader(value = "auth-code") code: String): ApiResponse<SignInResponse> {
        return ApiResponse.success(authService.signIn(code))
    }

    @PostMapping("/signup")
    fun signup(@RequestHeader(value = "auth-token") token: String,
               @RequestBody signUpRequest: SignUpRequest): ApiResponse<JwtTokens> {
        return ApiResponse.success(authService.signup(token, signUpRequest))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestHeader(value = "Refresh-Token") refreshToken: String): ApiResponse<JwtTokens> {
        return ApiResponse.success(authService.refresh(refreshToken))
    }

    @PostMapping("/logout")
    fun logout(
        @RequestHeader(value = "Authorization") accessToken: String,
        @RequestHeader(value = "Refresh-Token") refreshToken: String
    ): ApiResponse<Nothing> {
        authService.logout(accessToken, refreshToken)
        return ApiResponse.success()
    }

    @PostMapping("/withdrawal")
    fun withdraw(@CurrentMember member: Member): ApiResponse<Nothing> {
        authService.withdraw(member.id)
        return ApiResponse.success()
    }
}
