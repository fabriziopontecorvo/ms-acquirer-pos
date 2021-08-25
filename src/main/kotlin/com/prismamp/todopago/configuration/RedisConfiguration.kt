package com.prismamp.todopago.configuration

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.RedisSerializer

@Configuration
class RedisConfiguration {
    @Bean
    fun counterRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Long?> {
        val template = RedisTemplate<String, Long?>()

        template.setConnectionFactory(redisConnectionFactory)
        template.keySerializer = RedisSerializer.string()
        template.valueSerializer = GenericToStringSerializer(Long::class.java)

        return template
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<*, *> {
        val template = RedisTemplate<String, Any?>()
        template.setConnectionFactory(redisConnectionFactory)
        template.keySerializer = RedisSerializer.string()

        val objectMapper =
                ObjectMapper()
                        .registerKotlinModule()
                        .registerModule(JavaTimeModule())
                        .activateDefaultTyping(
                                BasicPolymorphicTypeValidator
                                        .builder()
                                        .allowIfBaseType(Any::class.java)
                                        .build(),
                                ObjectMapper.DefaultTyping.EVERYTHING,
                                JsonTypeInfo.As.PROPERTY
                        )
        template.valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        return template
    }

}
