package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_ADQUIRENTE_PERSISTENCE
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.ServiceCommunication
import com.prismamp.todopago.util.handleSuccess
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
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
                    .bimap(
                        leftOperation = { handleFailure() },
                        rightOperation = { handleSuccess(it) }
                    )
                    .bind()
                    .log { info("getBy: response {}", it) }
            }
        }

    private fun doGet(operationType: OperationType) =
        restClient.get(
            url = "$url/private/v1/operations/id?operation=${operationType}",
            entity = null,
            clazz = String::class.java
        )

    private fun handleFailure() =
        ServiceCommunication(APP_NAME, MS_ADQUIRENTE_PERSISTENCE)

}
