package com.yapp.web2.domain.jwt.application

import com.yapp.web2.domain.jwt.application.oauth.OAuthService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.dto.auth.response.SignInResponse
import com.yapp.web2.web.dto.auth.request.SignUpRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val memberRepository: MemberRepository,
    private val oAuthService: OAuthService,
    private val jwtService: JwtService
) {
    fun signIn(code: String): SignInResponse {
        val token = oAuthService.requestToken(code)
        val email = oAuthService.getUserEmail(token)

        val isExist = memberRepository.existsByEmail(email)
        if (!isExist) {
            return SignInResponse(false, JwtTokens(accessToken = token))
        }

        return SignInResponse(true, jwtService.issue(email))
    }

    @Transactional
    fun signup(token: String, signUpRequest: SignUpRequest): JwtTokens {
        val email = oAuthService.getUserEmail(token)
        join(email, signUpRequest)
        return jwtService.issue(email)
    }

    private fun join(email: String, signUpRequest: SignUpRequest) {
        memberRepository.save(
            Member(
                email = email,
                nickname = signUpRequest.nickname,
                jobCategory = signUpRequest.jobCategory,
                workingYears = signUpRequest.workingYears
            )
        )
    }
}
