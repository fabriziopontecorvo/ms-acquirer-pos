package com.prismamp.todopago.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.prismamp.todopago.configuration.http.RestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat

fun buildPactRestClient() =
    RestClient(RestTemplate(listOf(jackson2HttpMessageConverter())), RetryTemplate())

fun jackson2HttpMessageConverter() =
    Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
        .build<ObjectMapper>()
        .let {
            MappingJackson2HttpMessageConverter(it)
        }

val headers = HttpHeaders().apply { add("x-tenantId", "bimo") }
