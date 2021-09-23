package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.computations.either
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_ACQUIRER_BENEFIT
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.payment.adapter.repository.model.CheckBenefitRequest
import com.prismamp.todopago.payment.adapter.repository.rest.AccountClient.Companion.log
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.BenefitStatus
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.CheckBenefitError
import com.prismamp.todopago.util.ServiceCommunication
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpStatusCodeException

typealias BenefitNumber = String

@Component
class BenefitCheckClient(
    @Qualifier("defaultRestClient")
    private val restClient: RestClient
) {

    @Value("\${micro-services.acquirer-benefits.url}")
    private val url: String? = null

    suspend fun check(benefitNumber: BenefitNumber?, request: CheckBenefitRequest): Either<ApplicationError, Benefit?> =
        log.benchmark("check: checking benefit status") {
            either {
                benefitNumber?.run {
                    doGet(benefitNumber, request)
                        .bimap(
                            leftOperation = { handleFailure(it, benefitNumber) },
                            rightOperation = { benefitNumber.toDomain() }
                        ).bind()
                        .log { info("check: response {}", it) }
                }
            }
        }

    private fun handleFailure(
        it: HttpStatusCodeException,
        benefitNumber: BenefitNumber
    ) = when (it) {
        is HttpClientErrorException -> CheckBenefitError(benefitNumber)
        else -> ServiceCommunication(APP_NAME, MS_ACQUIRER_BENEFIT)
    }

    private fun doGet(
        benefitNumber: String,
        request: CheckBenefitRequest
    ) = restClient.get(
        url = url.plus("/private/recommendations/$benefitNumber/status/").plus(request.queryParamsToString()),
        clazz = Unit::class.java
    )

    private fun BenefitNumber.toDomain() = Benefit(BenefitStatus.OK, this)
}
