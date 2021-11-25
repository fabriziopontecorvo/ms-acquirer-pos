package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_LIMIT
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.payment.model.aLimitValidationRequest
import com.prismamp.todopago.payment.model.aLimitValidationResponse
import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationRequest
import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationResponse
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.ServiceCommunication
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

object LimitsClientSpec : Spek({

    Feature("limit validation") {

        lateinit var restClient: RestClient
        lateinit var limits: LimitsClient

        beforeEachScenario {
            restClient = mockk()
            limits = LimitsClient(restClient)
        }

        Scenario("limit validation response successfully") {

            lateinit var request: LimitValidationRequest
            lateinit var result: Either<ApplicationError, LimitValidationResponse>
            val accountId = 1L

            Given("a valid request") {
                request = aLimitValidationRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = "/limit/buyer/${accountId}/validation",
                        entity = HttpEntity(request),
                        clazz = LimitValidationResponse::class.java,
                    )
                } returns Right(ResponseEntity(aLimitValidationResponse(), OK))
            }

            When("execute validation") {
                result = runBlocking { limits.validation(request, accountId) }
            }

            Then("post was executed") {
                verify(exactly = 1) {
                    restClient.post(
                        url = "/limit/buyer/${accountId}/validation",
                        entity = HttpEntity(request),
                        clazz = LimitValidationResponse::class.java,
                    )
                }
            }

            And("response successfully") {
                result.should.be.equal(Right(aLimitValidationResponse()))
            }

        }

        Scenario("limit validation response fail") {

            lateinit var request: LimitValidationRequest
            lateinit var result: Either<ApplicationError, LimitValidationResponse>
            val accountId = 1L

            Given("a valid request") {
                request = aLimitValidationRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = "/limit/buyer/${accountId}/validation",
                        entity = HttpEntity(request),
                        clazz = LimitValidationResponse::class.java,
                    )
                } returns Left(HttpClientErrorException(INTERNAL_SERVER_ERROR))
            }

            When("execute validation") {
                result = runBlocking { limits.validation(request, accountId) }
            }

            Then("post was executed") {
                verify(exactly = 1) {
                    restClient.post(
                        url = "/limit/buyer/${accountId}/validation",
                        entity = HttpEntity(request),
                        clazz = LimitValidationResponse::class.java,
                    )
                }
            }

            And("response failure") {
                result.should.be.equal(Left(ServiceCommunication(APP_NAME, MS_LIMIT)))
            }

        }

    }

})
