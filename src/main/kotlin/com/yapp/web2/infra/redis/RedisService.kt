package com.yapp.web2.infra.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService (
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun setValue(key: String, value: String, time: Long) {
        val values = redisTemplate.opsForValue()
        values.set(key, value, Duration.ofMillis(time))
    }

    fun getValue(key: String): Any? {
        val values = redisTemplate.opsForValue()
        return values.get(key)
    }

    fun deleteValue(key: String) {
        redisTemplate.delete(key)
    }
}
