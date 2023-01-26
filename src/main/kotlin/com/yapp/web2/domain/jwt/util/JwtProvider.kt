package com.yapp.web2.domain.jwt.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
@Transactional(readOnly = true)
class JwtProvider(
    @Value("\${jwt.secret}") private val SECRET_KEY: String,
    @Value("\${jwt.access-token-expiry}") private val accessTokenValidTime: Int,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenValidTime: Long
) {
    private val key: Key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(memberId: Long, email: String): String {
        val now = Date()
        return Jwts.builder()
            .setIssuedAt(now)
            .setExpiration(Date(now.time + accessTokenValidTime))
            .signWith(key)
            .claim("id", memberId)
            .claim("email", email)
            .compact()
    }

    fun createRefreshToken(): String {
        val now = Date()
        return Jwts.builder()
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshTokenValidTime))
            .signWith(key)
            .compact()
    }

    fun parseToken(token: String): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
    }

    fun isExpired(token: String, date: Date): Boolean {
        return try {
            val claims: Jws<Claims> = parseToken(token)
            !claims.body.expiration.before(date)
        } catch (e: Exception) {
            false
        }
    }
}
