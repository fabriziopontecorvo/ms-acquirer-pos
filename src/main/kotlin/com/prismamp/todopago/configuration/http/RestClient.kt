package com.prismamp.todopago.configuration.http

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

class RestClient(
    private val template: RestTemplate,
    private val retryTemplate: RetryTemplate
) {
    companion object : CompanionLogger() {
        const val REST_CALL_MSG = "rest call"
        const val REST_CALL_RETRY = "rest call retry instance"
    }

    fun <T> post(url: String, request: Any, clazz: Class<T>) =
        executeCall {
            log.info("exchanging POST {} with body: {} ", url, request.toString())
            template.exchange(url, HttpMethod.POST, HttpEntity(request), clazz)
        }

    fun <T> post(url: String, entity: HttpEntity<Any>, clazz: Class<T>) =
        executeCall {
            log.info("exchanging POST $url, ${entity.body}")
            template.exchange(url, HttpMethod.POST, entity, clazz)
        }

    fun <T> put(url: String, request: Any, clazz: Class<T>) =
        executeCall {
            log.info("exchanging PUT {} with body: {} ", url, request.toString())
            template.exchange(url, HttpMethod.PUT, HttpEntity(request), clazz)
        }

    fun <T> get(url: String, entity: HttpEntity<Any>? = null, clazz: Class<T>) =
        executeCall {
            log.info("exchanging GET {}", url)
            template.exchange(url, HttpMethod.GET, entity, clazz)
        }

    private fun <T> executeCall(restCall: RestOperations.() -> T): Either<HttpStatusCodeException, T> =
        try {
            Right(log.benchmark(REST_CALL_MSG) {
                retryTemplate.execute<T, Throwable> {
                    log.benchmark(REST_CALL_RETRY) {
                        restCall(template)
                            .log {
                                info("executeCall: Response: {}", it.toString())
                            }
                    }
                }
            }
            )
        } catch (e: HttpStatusCodeException) {
            log.warn("exception thrown: $e")
            Left(e)
        }
}
