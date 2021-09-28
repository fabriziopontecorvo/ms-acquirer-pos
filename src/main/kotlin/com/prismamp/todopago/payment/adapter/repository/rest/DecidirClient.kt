package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.*
import arrow.core.computations.either
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.DECIDIR
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.enum.PaymentStatusRequest.*
import com.prismamp.todopago.payment.adapter.repository.model.DecidirResponse
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.ServiceCommunication
import com.prismamp.todopago.util.UnprocessableEntity
import com.prismamp.todopago.util.handleSuccess
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import com.prismamp.todopago.util.tenant.TenantAwareDecidirComponent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Repository
import org.springframework.web.client.HttpStatusCodeException
import java.text.SimpleDateFormat

@Repository
class DecidirClient(
    @Qualifier("defaultRestClient")
    private val restClient: RestClient,
    private val tenantAwareDecidirComponent: TenantAwareDecidirComponent,
) {

    @Value("\${decidir.lapos.url}")
    var url: String = ""

    @Value("\${decidir.lapos.path.payment}")
    var paymentPath: String = ""

    companion object : CompanionLogger() {
        private val DECIDIR_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }

    suspend fun executePayment(request: GatewayRequest): Either<ApplicationError, GatewayResponse> =
        log.benchmark("executePayment") {
            either {
                doPost<DecidirResponse>(request, paymentPath)
                    .bimap(
                        leftOperation = { handleFailure(it) },
                        rightOperation = { handleSuccess(it).toDomain(SUCCESS) }
                    )
                    .leftFlatten()
                    .bind()
                    .log { info("executePayment: result {}", it) }
            }
        }

    private inline fun <reified T> doPost(request: Any, path: String) =
        restClient.post(
            url = url + path,
            entity = tenantAwareDecidirComponent.buildEntity(request),
            clazz = T::class.java,
        )

    private fun handleFailure(status: HttpStatusCodeException): Either<ApplicationError, GatewayResponse> =
        when (status.statusCode) {
            REQUEST_TIMEOUT, GATEWAY_TIMEOUT ->
                DecidirResponse()
                    .toDomain(statusRequest = PENDING).right()
            PAYMENT_REQUIRED ->
                mapResponse<DecidirResponse>(status.responseBodyAsString)
                    .toDomain(statusRequest = FAILURE).right()
            BAD_REQUEST, UNPROCESSABLE_ENTITY, NOT_FOUND -> UnprocessableEntity(status.responseBodyAsString).left()
            else -> ServiceCommunication(APP_NAME, DECIDIR).left()

        }

    private inline fun <reified T> mapResponse(json: String): T =
        with(ObjectMapper()) {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            dateFormat = DECIDIR_DATE_FORMAT

            this
        }.readValue(json, T::class.java)


    private fun <T> Either<Either<ApplicationError, T>, T>.leftFlatten(): Either<ApplicationError, T> =
        when(this){
            is Either.Right -> value.right()
            is Either.Left -> value
        }
}
