package com.yapp.web2.domain.jwt.util

import com.yapp.web2.domain.jwt.user.UserDetailsImpl
import com.yapp.web2.domain.jwt.user.UserDetailsServiceImpl
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
@Transactional(readOnly = true)
class JwtUtil(
    @Value("\${jwt.access-token-header}") private val ACCESS_TOKEN_HEADER: String,
    @Value("\${jwt.refresh-token-header}") private val REFRESH_TOKEN_HEADER: String,
    private val userDetailsService: UserDetailsServiceImpl,
    private val jwtProvider: JwtProvider
) {
    fun getAuthentication(token: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getUserPk(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getFromSecurityContextHolder(): UserDetailsImpl {
        return SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
    }

    fun resolveAccessToken(request: HttpServletRequest): String {
        return request.getHeader(ACCESS_TOKEN_HEADER).replace("Bearer", "").trim()
    }

    fun resolveRefreshToken(request: HttpServletRequest): String {
        return request.getHeader(REFRESH_TOKEN_HEADER)
    }

    fun isExpired(token: String, date: Date): Boolean {
        return try {
            val claims: Jws<Claims> = jwtProvider.parseToken(token)
            !claims.body.expiration.before(date)
        } catch (e: Exception) {
            false
        }
    }

    private fun getUserPk(token: String): String {
        return try {
            jwtProvider.parseToken(token).body.subject
        } catch (e: ExpiredJwtException) {
            e.claims.subject
        }
    }
}
