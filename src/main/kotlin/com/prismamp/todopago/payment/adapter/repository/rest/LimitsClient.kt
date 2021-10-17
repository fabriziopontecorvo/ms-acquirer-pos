package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_LIMIT
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationRequest
import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationResponse
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.ServiceCommunication
import com.prismamp.todopago.util.handleSuccess
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Repository

@Repository
class LimitsClient(
    @Qualifier("defaultRestClient") val restClient: RestClient
) {

    companion object : CompanionLogger()

    @Value("\${micro-services.limit.url}")
    var url: String = ""

    @Value("\${micro-services.limit.path}") // "/limit/buyer/${accountId}/validation"
    var path: String = ""

    suspend fun validation(
        request: LimitValidationRequest,
        accountId: Long
    ): Either<ApplicationError, LimitValidationResponse> =
        log.benchmark("Limit validation") {
            either {
                doPost<LimitValidationResponse>(request, path)
                    .bimap(
                        leftOperation = { handleFailure() },
                        rightOperation = { handleSuccess(it) }
                    )
                    .bind()
                    .log { info("validation: response {}", it) }
            }
        }

    private inline fun <reified T> doPost(request: Any, path: String) =
        restClient.post(
            url = url + path,
            entity = HttpEntity(request),
            clazz = T::class.java,
        )

    private fun handleFailure() =
        ServiceCommunication(APP_NAME, MS_LIMIT)

}