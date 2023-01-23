package com.yapp.web2.infra.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") val redisHost: String,
    @Value("\${spring.data.redis.port}") val redisPort: Int
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(redisConnectionFactory())
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        return redisTemplate
    }
}
