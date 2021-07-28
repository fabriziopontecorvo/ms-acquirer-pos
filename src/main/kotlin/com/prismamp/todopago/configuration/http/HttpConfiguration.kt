package com.prismamp.todopago.configuration.http

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.prismamp.todopago.commons.rest.handler.ValidationError
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.retry.backoff.BackOffPolicy
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.time.Duration

@Configuration
class HttpConfiguration {

    @Primary
    @Bean("intentionMapperBuilder")
    fun jackson2ObjectMapperBuilder() = Jackson2ObjectMapperBuilder()
        .failOnUnknownProperties(false)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .featuresToEnable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))

    @Bean
    fun validationError() = ValidationError { fieldError -> "Field '${fieldError.field}' is invalid" }

    @Bean
    fun retryTemplate(
        @Value("\${rest-client.retry.max-attempts:3}") maxAttempts: Int,
        backOffPolicy: BackOffPolicy
    ) = RetryTemplate().apply {
        setRetryPolicy(InternalServerExceptionClassifierRetryPolicy(maxAttempts))
        setBackOffPolicy(backOffPolicy)
    }

    @Bean
    fun backOffPolicy(
        @Value("\${rest-client.retry.backoff-initial-interval:500}") backoffInitialInterval: Long,
        @Value("\${rest-client.retry.backoff-max-interval: 500}") backoffMaxInterval: Long,
        @Value("\${rest-client.retry.backoff-multiplier:1.0}") backoffMultiplier: Double
    ) = ExponentialBackOffPolicy().apply {
        initialInterval = backoffInitialInterval
        maxInterval = backoffMaxInterval
        multiplier = backoffMultiplier
    }

    @Bean("defaultRestTemplate")
    fun defaultRestTemplate(
        @Value("\${rest-client.timeout.read:15}") readTimeout: Long,
        @Value("\${rest-client.timeout.connection:10}") connectionTimeout: Long,
        factory: ClientHttpRequestFactory,
        restTemplateBuilder: RestTemplateBuilder
    ): RestTemplate = restTemplateBuilder.requestFactory { factory }
        .setConnectTimeout(Duration.ofSeconds(connectionTimeout))
        .setReadTimeout(Duration.ofSeconds(readTimeout))
        .build()

    @Bean("decidirRestTemplate")
    fun decidirRestTemplate(
        @Value("\${rest-client.timeout.decidir.read:15000}") readTimeout: Long,
        @Value("\${rest-client.timeout.decidir.connection:10000}") connectionTimeout: Long,
        factory: ClientHttpRequestFactory,
        restTemplateBuilder: RestTemplateBuilder
    ): RestTemplate = restTemplateBuilder.requestFactory { factory }
        .setConnectTimeout(Duration.ofMillis(connectionTimeout))
        .setReadTimeout(Duration.ofMillis(readTimeout))
        .build()

    @Bean
    fun createRequestFactory(
        connectionManager: PoolingHttpClientConnectionManager
    ): ClientHttpRequestFactory =
        HttpComponentsClientHttpRequestFactory(
            HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build()
        ).also {
            val monitor = IdleConnectionMonitorThread(connectionManager)
            monitor.start()
            monitor.join(1000)
        }

    @Bean
    fun createConnectionManager(
        @Value("\${rest-client.limits.pool.max-per-route:100}") maxPerRoute: Int,
        @Value("\${rest-client.limits.pool.max-total:200}") max: Int
    ) = PoolingHttpClientConnectionManager().apply {
        defaultMaxPerRoute = maxPerRoute
        maxTotal = max
    }

    @Bean("defaultRestClient")
    fun defaultRestClient(
        @Qualifier("defaultRestTemplate") restTemplate: RestTemplate,
        retryTemplate: RetryTemplate
    ) = RestClient(restTemplate, retryTemplate)

    @Bean("decidirRestClient")
    fun decidirRestClient(
        @Qualifier("decidirRestTemplate") restTemplate: RestTemplate,
        retryTemplate: RetryTemplate
    ) = RestClient(restTemplate, retryTemplate)
}
