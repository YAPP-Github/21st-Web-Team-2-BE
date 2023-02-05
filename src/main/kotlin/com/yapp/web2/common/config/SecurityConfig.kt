package com.yapp.web2.common.config

import com.yapp.web2.domain.jwt.interceptor.JwtInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
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
                "/api/v1/auth/signin",
                "/api/v1/auth/signup",
                "/api/v1/auth/refresh",
                "/api/v1/topic/popular",
                "/api/v1/topic/latest",
                "/api/v1/topic/{topicId}",
                "/api/v1/comment/**",
                "/docs/**",
                "/api/v1/nickname-duplication",
            )    //TODO 회원 도입 후 경로 제거
    }
}
