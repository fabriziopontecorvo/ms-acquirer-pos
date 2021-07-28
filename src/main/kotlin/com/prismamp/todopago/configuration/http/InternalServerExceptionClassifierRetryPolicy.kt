package com.prismamp.todopago.configuration.http

import org.springframework.http.HttpStatus
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy
import org.springframework.retry.policy.NeverRetryPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.web.client.HttpServerErrorException

internal class InternalServerExceptionClassifierRetryPolicy(
    maxAttempts: Int
) : ExceptionClassifierRetryPolicy() {
    init {
        val simpleRetryPolicy = SimpleRetryPolicy()
        simpleRetryPolicy.maxAttempts = maxAttempts

        this.setExceptionClassifier { classifiable ->
            if (classifiable is HttpServerErrorException) {
                if (classifiable.statusCode == HttpStatus.INTERNAL_SERVER_ERROR || classifiable
                        .statusCode == HttpStatus.GATEWAY_TIMEOUT
                ) {
                    simpleRetryPolicy
                } else NeverRetryPolicy()
            } else NeverRetryPolicy()
        }
    }
}
