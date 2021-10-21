package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.MS_ADQUIRENTE_PERSISTENCE
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.handleFailure
import com.prismamp.todopago.util.handleSuccess
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository

@Repository
class IdProviderClient(
    @Qualifier("defaultRestClient")
    private val restClient: RestClient
) {
    companion object : CompanionLogger()

    @Value("\${micro-services.adquirente-persistence.url}")
    var url: String = ""

    suspend fun getId(operationType: OperationType): Either<ApplicationError, String?> =
        log.benchmark("Get id of payment") {
            either {
                doGet(operationType)
                    .handleCallback()
                    .log { info("getBy: response {}", it) }
                    .bind()
            }
        }

    private fun doGet(operationType: OperationType) =
        restClient.get(
            url = "$url/private/v1/operations/id?operation=${operationType}",
            entity = null,
            clazz = String::class.java
        )

    private fun Either<Throwable, ResponseEntity<String>>.handleCallback() =
        bimap(
            leftOperation = { it.handleFailure(MS_ADQUIRENTE_PERSISTENCE) },
            rightOperation = { it.handleSuccess() }
        )

}
