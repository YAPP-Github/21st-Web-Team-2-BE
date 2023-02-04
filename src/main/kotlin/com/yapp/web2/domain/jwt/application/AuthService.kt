package com.yapp.web2.domain.jwt.application

import com.yapp.web2.domain.jwt.application.oauth.OAuthService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.infra.redis.RedisService
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.dto.auth.response.SignInResponse
import com.yapp.web2.web.dto.auth.request.SignUpRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val memberRepository: MemberRepository,
    private val oAuthService: OAuthService,
    private val jwtService: JwtService,
    private val redisService: RedisService
) {
    @Value("\${jwt.refresh-token-expiry}")
    private val refreshTokenExpiry: Long = 0

    @Value("\${jwt.access-token-expiry}")
    private val accessTokenExpiry: Long = 0

    @Transactional
    fun signIn(code: String): SignInResponse {
        val token = oAuthService.requestToken(code)
        val email = oAuthService.getUserEmail(token)

        val isExist = memberRepository.existsByEmail(email)
        if (!isExist) {
            return SignInResponse(false, JwtTokens(accessToken = token))
        }

        val tokens = jwtService.issue(email)
        storeRefresh(tokens, email)
        return SignInResponse(true, tokens)
    }

    @Transactional
    fun signup(token: String, signUpRequest: SignUpRequest): JwtTokens {
        val email = oAuthService.getUserEmail(token)
        join(email, signUpRequest)

        val tokens = jwtService.issue(email)
        storeRefresh(tokens, email)
        return tokens
    }

    @Transactional
    fun refresh(refreshToken: String): JwtTokens {
        val email = redisService.getValue("$REFRESH_TOKEN_PREFIX:${refreshToken}") as String?
            ?: throw BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN)

        val member: Member = memberRepository.findByEmail(email)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND)

        val jwtToken = jwtService.refresh(refreshToken, email, member.id)
        storeRefresh(jwtToken, member.email)
        redisService.deleteValue("$REFRESH_TOKEN_PREFIX:${refreshToken}")
        return jwtToken
    }

    @Transactional
    fun logout(accessToken: String, refreshToken: String) {
        redisService.getValue("$REFRESH_TOKEN_PREFIX:$refreshToken")
            ?: throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)
        redisService.deleteValue("$REFRESH_TOKEN_PREFIX:$refreshToken")

        storeLogoutAccessToken(accessToken)
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

    private fun storeRefresh(jwtToken: JwtTokens, email: String) {
        redisService.setValue(
            "$REFRESH_TOKEN_PREFIX:${jwtToken.refreshToken}",
            email,
            refreshTokenExpiry
        )
    }

    private fun storeLogoutAccessToken(accessToken: String) {
        redisService.setValue(
            "$LOGOUT_ACCESS_TOKEN_PREFIX:${accessToken}",
            "logout",
            accessTokenExpiry
        )
    }

    companion object {
        const val REFRESH_TOKEN_PREFIX = "refresh"
        const val LOGOUT_ACCESS_TOKEN_PREFIX = "logout"
    }
}
