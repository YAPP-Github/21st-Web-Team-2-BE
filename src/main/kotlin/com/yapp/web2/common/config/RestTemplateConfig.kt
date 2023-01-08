package com.yapp.web2.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate


@Configuration
class RestTemplateConfig {
    @Bean
    fun restTemplateBuilder(): RestTemplateBuilder {
        return RestTemplateBuilder()
    }

    @Bean
    fun restTemplate(): RestTemplate? {
        return RestTemplate()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
