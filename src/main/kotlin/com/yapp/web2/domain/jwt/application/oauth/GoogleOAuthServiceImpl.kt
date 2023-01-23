package com.yapp.web2.domain.jwt.application.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.web2.common.util.logger
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
@Transactional(readOnly = true)
class GoogleOAuthServiceImpl(
    private val restTemplate: RestTemplate,
) : OAuthService {
    val objectMapper: ObjectMapper = ObjectMapper()

    @Value("\${spring.OAuth2.google.url.token}")
    private lateinit var GOOGLE_TOKEN_REQUEST_URL: String

    @Value("\${spring.OAuth2.google.client_id}")
    private lateinit var GOOGLE_CLIENT_ID: String

    @Value("\${spring.OAuth2.google.client_secret}")
    private lateinit var GOOGLE_CLIENT_SECRET: String

    @Value("\${spring.OAuth2.google.url.redirect}")
    private lateinit var GOOGLE_REDIRECT_URI: String

    @Value("\${spring.OAuth2.google.url.profile}")
    private lateinit var GOOGLE_PROFILE_URI: String

    override fun requestToken(authCode: String): String {
        val params = buildParam(authCode)
        val response: ResponseEntity<String> = try {
            restTemplate.postForEntity(
                GOOGLE_TOKEN_REQUEST_URL,
                params,
                String::class.java)
        } catch (e: HttpClientErrorException) {
            logger().info(e.message)
            throw BusinessException(ErrorCode.OAUTH2_FAIL_EXCEPTION)
        }
        return objectMapper
            .readValue(response.body, GoogleOAuthToken::class.java)
            .access_token!!
    }

    override fun getUserEmail(token: String): String {
        val response: ResponseEntity<String> = try {
            restTemplate.postForEntity(
                GOOGLE_PROFILE_URI,
                buildProfileRequest(token),
                String::class.java)
        } catch (e: HttpClientErrorException) {
            throw BusinessException(ErrorCode.OAUTH2_FAIL_EXCEPTION)
        }
        return objectMapper.readTree(response.body)
            .get("email").asText()
    }

    private fun buildProfileRequest(token: String): HttpEntity<*> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.setBearerAuth(token)
        return HttpEntity(null, headers)
    }

    private fun buildParam(authCode: String): Map<String, String> {
        return mapOf(
            "code" to authCode,
            "client_id" to GOOGLE_CLIENT_ID,
            "client_secret" to GOOGLE_CLIENT_SECRET,
            "redirect_uri" to GOOGLE_REDIRECT_URI,
            "grant_type" to "authorization_code"
        )
    }
}
