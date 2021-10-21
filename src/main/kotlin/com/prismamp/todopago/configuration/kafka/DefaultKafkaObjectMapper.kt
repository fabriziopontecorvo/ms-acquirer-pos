package com.prismamp.todopago.configuration.kafka

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.prismamp.todopago.commons.queues.KafkaObjectMapper


class DefaultKafkaObjectMapper : KafkaObjectMapper {

    companion object {
        fun objectMapper(): ObjectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
    }


    inline fun <reified V> deserialize(value: String): V =
        objectMapper().readValue(value, jacksonTypeRef<V>())

    override fun <K> serializeKey(value: K): String =
        value.toString()

    override fun <V> serializeValue(value: V): String =
        objectMapper().writeValueAsString(value)
}
