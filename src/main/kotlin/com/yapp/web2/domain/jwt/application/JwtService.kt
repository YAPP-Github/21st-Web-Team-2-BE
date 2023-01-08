package com.yapp.web2.domain.jwt.application

import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.member.application.MemberService
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.stereotype.Service

@Service
class JwtService(
    private val jwtProvider: JwtProvider,
    private val memberService: MemberService,
) {
    fun issue(email: String): JwtTokens {
        memberService.findByEmail(email)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)

        val accessToken: String = jwtProvider.createAccessToken(email)
        val refreshToken: String = jwtProvider.createRefreshToken()

        return JwtTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
