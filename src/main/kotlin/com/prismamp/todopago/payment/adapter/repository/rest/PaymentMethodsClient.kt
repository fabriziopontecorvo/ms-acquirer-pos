package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_PAYMENT_METHODS
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.payment.adapter.repository.model.PaymentMethodResponse
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.*
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException

@Component
class PaymentMethodsClient(
    @Qualifier("defaultRestClient")
    val restClient: RestClient
) {

    companion object : CompanionLogger()

    @Value("\${micro-services.payment-method.url}")
    var url: String = ""

    suspend fun getPaymentMethod(accountId: String, paymentMethod: String): Either<ApplicationError, PaymentMethod> =
        log.benchmark("getPaymentMethod: search wallet") {
            either {
                doGet(accountId, paymentMethod)
                    .handleCallback(accountId)
                    .log { info("getPaymentMethod: response", it) }
                    .bind()
            }
        }

    private fun doGet(accountId: String, paymentMethod: String) =
        restClient.get(
            url = "$url/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
            clazz = PaymentMethodResponse::class.java
        )

    private fun Either<Throwable, ResponseEntity<PaymentMethodResponse>>.handleCallback(accountId: String) =
        bimap(
            leftOperation = {
                it.handleFailure(MS_PAYMENT_METHODS) { error ->
                    handleHttpFailure(error, accountId)
                }
            },
            rightOperation = { it.handleSuccess().toDomain() }
        )

    private fun handleHttpFailure(status: HttpStatusCodeException, paymentMethod: String) =
        when (status.statusCode) {
            NOT_FOUND -> InvalidPaymentMethod(paymentMethod)
            else -> ServiceCommunication(APP_NAME, MS_PAYMENT_METHODS)
        }

}
