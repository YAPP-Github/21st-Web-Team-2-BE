package com.yapp.web2.domain.jwt.application

import com.yapp.web2.common.util.findByIdOrThrow
import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.member.application.MemberService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtProvider: JwtProvider,
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
) {
    fun issue(email: String): JwtTokens {
        val member = memberService.findByEmail(email)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)

        val accessToken: String = jwtProvider.createAccessToken(member.id, email)
        val refreshToken: String = jwtProvider.createRefreshToken()

        return JwtTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun findAccessTokenMember(accessToken: String): Member {
        val claimsJws = jwtProvider.parseToken(accessToken)
        val memberId = claimsJws.body["id"].toString().toLong()

        return memberRepository.findByIdOrThrow(memberId)
    }

    fun refresh(refreshToken: String, email: String, memberId: Long): JwtTokens {
        if (isValidate(refreshToken)) {
            throw BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN)
        }
        val accessToken = jwtProvider.createAccessToken(memberId, email)

        var localRefreshToken = refreshToken
        if (isRefreshable(refreshToken)) {
            localRefreshToken = jwtProvider.createRefreshToken()
        }
        return JwtTokens(accessToken, localRefreshToken)
    }

    private fun isValidate(refreshToken: String): Boolean {
        val now = Date()
        return !jwtProvider.isExpired(refreshToken, now)
    }

    private fun isRefreshable(refreshToken: String): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        calendar.time = Date()
        calendar.add(Calendar.DATE, 3)
        return !jwtProvider.isExpired(refreshToken, calendar.time)
    }
}
