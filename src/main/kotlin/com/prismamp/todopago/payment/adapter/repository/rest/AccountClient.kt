package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_ACCOUNT
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.payment.adapter.repository.model.AccountResponse
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.InvalidAccount
import com.prismamp.todopago.util.ServiceCommunication
import com.prismamp.todopago.util.handleSuccess
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException

@Component
class AccountClient(
    @Qualifier("defaultRestClient")
    val restClient: RestClient
) {

    companion object : CompanionLogger()

    @Value("\${micro-services.account.url}")
    var url: String = ""

    suspend fun getAccountBy(accountId: String): Either<ApplicationError, Account> =
        log.benchmark("getAccountBy: search account by id") {
            either{
                doGet(accountId)
                    .bimap(
                        leftOperation = { handleFailure(it, accountId) },
                        rightOperation = { handleSuccess(it).toDomain() }
                    ).bind()
                    .log { info("getAccountBy: response {}", it) }
            }
        }

    private fun handleFailure(
        it: HttpStatusCodeException,
        accountId: String
    ) = when (it.statusCode) {
        HttpStatus.NOT_FOUND -> InvalidAccount(accountId)
        else -> ServiceCommunication(APP_NAME, MS_ACCOUNT)
    }

    private fun doGet(accountId: String): Either<HttpStatusCodeException, ResponseEntity<AccountResponse>> =
        restClient.get(
            url = "$url/v3/accounts/$accountId",
            clazz = AccountResponse::class.java
        )


}
