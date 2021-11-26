package com.prismamp.todopago.transfer.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.MS_COELSA
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.transfer.adapter.repository.model.CoelsaTransferRequest
import com.prismamp.todopago.transfer.adapter.repository.model.CoelsaTransferResponse
import com.prismamp.todopago.transfer.domain.model.Transfer
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
class CoelsaClient(
    @Qualifier("defaultRestClient")
    private val restClient: RestClient
) {

    companion object : CompanionLogger()

    @Value("\${coelsa.url}")
    var url: String = ""

    @Value("\${coelsa.transfer.path}")
    var transferPath: String = ""

    suspend fun makeTransfer(request: CoelsaTransferRequest): Either<ApplicationError, Transfer> =
        log.benchmark("makeTransfer: perform request to coelsa") {
            either {
                doPost(request)
                    .handleCallback()
                    .log { info("makeTransfer: result {}", it) }
                    .bind()
            }
        }

    private fun doPost(request: CoelsaTransferRequest) =
        restClient.post(
            url = "$url$transferPath",
            request = request,
            clazz = CoelsaTransferResponse::class.java
        )

    private fun Either<Throwable, ResponseEntity<CoelsaTransferResponse>>.handleCallback() =
        bimap(
            leftOperation = { it.handleFailure(MS_COELSA) },
            rightOperation = { it.handleSuccess().toDomain() }
        )
}
