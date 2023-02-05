package com.yapp.web2.domain.jwt.interceptor

import com.yapp.web2.common.annotation.NonMember
import com.yapp.web2.domain.jwt.application.AuthService.Companion.LOGOUT_ACCESS_TOKEN_PREFIX
import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.jwt.util.JwtUtil
import com.yapp.web2.infra.redis.RedisService
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(
    private val jwtUtil: JwtUtil,
    private val jwtProvider: JwtProvider,
    private val redisService: RedisService
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as HandlerMethod
        val accessToken = request.getHeader("Authorization")
        if (handlerMethod.getMethodAnnotation(NonMember::class.java) != null
            && (accessToken == null || accessToken.isBlank())
        ) {
            return true
        }

        try {
            val accessToken = jwtUtil.resolveAccessToken(request)
            jwtProvider.parseToken(accessToken)
            if (isLogout(accessToken)) {
                throw BusinessException(ErrorCode.INVALID_JWT)
            }
            setAuthentication(accessToken)
        } catch (e: ExpiredJwtException) {
            throw BusinessException(ErrorCode.EXPIRED_JWT)
        } catch (e: NullPointerException) {
            throw BusinessException(ErrorCode.NULL_JWT)
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.INVALID_JWT)
        }
        return true
    }

    private fun setAuthentication(accessToken: String) {
        val authentication = jwtUtil.getAuthentication(accessToken)
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun isLogout(accessToken: String): Boolean {
        redisService.getValue("${LOGOUT_ACCESS_TOKEN_PREFIX}:$accessToken")
            ?: return false
        return true
    }
}
