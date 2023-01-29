package com.yapp.web2.common.config

import com.yapp.web2.domain.jwt.interceptor.JwtInterceptor
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@TestConfiguration
@EnableWebSecurity
class SecurityConfig(
    private val jwtInterceptor: JwtInterceptor
) : WebMvcConfigurer {
    @Bean
    fun filterChain(http: HttpSecurity?): SecurityFilterChain {
        http!!
            .httpBasic().disable()
            .csrf().disable()
        return http.build()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/api/v1/auth/**",
                "/api/v1/topic/**",
                "/api/v1/comment/**",
                "/docs/**",
                "/api/v1/nickname-duplication",
                "/api/v1/vote/option"
            )
    }
}
