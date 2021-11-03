package com.prismamp.todopago.payment.adapter.repository.rest

import aDecidirResponse
import aGatewayRequest
import aGatewayResponse
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.DECIDIR
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.enum.PaymentStatusRequest.FAILURE
import com.prismamp.todopago.enum.PaymentStatusRequest.PENDING
import com.prismamp.todopago.payment.adapter.repository.model.DecidirErrorResponse
import com.prismamp.todopago.payment.adapter.repository.model.DecidirResponse
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.DecidirError
import com.prismamp.todopago.util.ServiceCommunication
import com.prismamp.todopago.util.tenant.TenantAwareDecidirComponent
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import java.nio.charset.Charset.defaultCharset

object DecidirClientSpec : Spek({

    Feature("execute payment") {

        lateinit var restClient: RestClient
        lateinit var decidirClient: DecidirClient
        lateinit var tenantAwareDecidirComponent: TenantAwareDecidirComponent
        lateinit var url: String
        lateinit var path: String

        beforeEachScenario {
            restClient = mockk()
            tenantAwareDecidirComponent = mockk()
            url = "http://someUrl:8080/"
            path = "some/path"
            decidirClient = DecidirClient(restClient, tenantAwareDecidirComponent).also {
                it.url = url
                it.paymentPath = path
            }
        }

        Scenario("execute payment successfully") {

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            lateinit var expectedValue: GatewayResponse
            Given("a valid request") {
                request = aGatewayRequest()
                expectedValue = aGatewayResponse()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                } returns Right(ResponseEntity(aDecidirResponse(), HttpStatus.OK))
            }

            When("call execute payment") {
                result = runBlocking { decidirClient.executePayment(request) }
            }

            Then("restClient was called") {
                verify(exactly = 1) {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                }
            }

            And("response successfully") {
                result.should.be.equal(Right(expectedValue))
            }

        }

        Scenario("execute payment pending") {

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            Given("a valid request") {
                request = aGatewayRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                } returns Left(ResourceAccessException(""))
            }

            When("call execute payment") {
                result = runBlocking { decidirClient.executePayment(request) }
            }

            Then("restClient was called") {
                verify(exactly = 1) {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                }
            }

            And("response pending") {
                result.map {
                    it.statusRequest.should.be.equal(PENDING)
                }
            }

        }

        Scenario("execute payment REQUEST_TIMEOUT") {

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            Given("a valid request") {
                request = aGatewayRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                } returns Left(HttpClientErrorException(REQUEST_TIMEOUT))
            }

            When("call execute payment") {
                result = runBlocking { decidirClient.executePayment(request) }
            }

            Then("restClient was called") {
                verify(exactly = 1) {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                }
            }

            And("response pending") {
                result.map {
                    it.statusRequest.should.be.equal(PENDING)
                }
            }

        }

        Scenario("execute payment failure") {

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            Given("a valid request") {
                request = aGatewayRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                } returns Left(
                    HttpClientErrorException(PAYMENT_REQUIRED, "402", "{\"id\":2}".toByteArray(), defaultCharset())
                )
            }

            When("call execute payment") {
                result = runBlocking { decidirClient.executePayment(request) }
            }

            Then("restClient was called") {
                verify(exactly = 1) {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                }
            }

            And("response failure") {
                result.map {
                    it.statusRequest.should.be.equal(FAILURE)
                    it.id.should.be.equal(2)
                }
            }
        }

        Scenario("unprocessable payment") {

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            Given("a valid request") {
                request = aGatewayRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                } returns Left(
                    HttpClientErrorException(NOT_FOUND, "404", "{\"error_type\": \"param\"}".toByteArray(), defaultCharset())
                )
            }

            When("call execute payment") {
                result = runBlocking { decidirClient.executePayment(request) }
            }

            Then("restClient was called") {
                verify(exactly = 1) {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                }
            }

            And("response failure") {
                result.should.be.equal(
                    Left(DecidirError(DecidirErrorResponse(errorType = "param")))
                )
            }

        }

        Scenario("service communication exeption") {

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            Given("a valid request") {
                request = aGatewayRequest()
            }

            And("mock post") {
                every {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                } returns Left(
                    HttpClientErrorException(INTERNAL_SERVER_ERROR)
                )
            }

            When("call execute payment") {
                result = runBlocking { decidirClient.executePayment(request) }
            }

            Then("restClient was called") {
                verify(exactly = 1) {
                    restClient.post(
                        url = url + path,
                        entity = tenantAwareDecidirComponent.buildEntity(request),
                        clazz = DecidirResponse::class.java,
                    )
                }
            }

            And("response failure") {
                result.should.be.equal(
                    Left(ServiceCommunication(APP_NAME, DECIDIR))
                )
            }

        }


    }

})
