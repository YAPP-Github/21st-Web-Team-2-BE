package com.yapp.web2.domain.jwt.application

import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.jwt.util.JwtUtil
import com.yapp.web2.domain.member.application.MemberService
import com.yapp.web2.web.api.dto.jwt.response.JwtTokens
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtProvider: JwtProvider,
    private val jwtUtil: JwtUtil,
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

	fun getRemainExpiry(token: String): Long {
		val expiration = jwtProvider.parseToken(token).body.expiration
		val now = Date()
		return expiration.time - now.time
	}

	private fun isValidate(refreshToken: String): Boolean {
		val now = Date()
		return !jwtUtil.isExpired(refreshToken, now)
	}

	private fun isRefreshable(refreshToken: String): Boolean {
		val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
		calendar.time = Date()
		calendar.add(Calendar.DATE, 3)
		return !jwtUtil.isExpired(refreshToken, calendar.time)
	}
}
