package com.yapp.web2.domain.jwt.interceptor

import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.jwt.util.JwtUtil
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(
    private val jwtUtil: JwtUtil,
    private val jwtProvider: JwtProvider
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        try {
            val accessToken = jwtUtil.resolveAccessToken(request)
            jwtProvider.parseToken(accessToken)
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
}
