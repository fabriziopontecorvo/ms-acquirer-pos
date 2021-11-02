package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.Constants
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_PAYMENT_METHODS
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.model.aPaymentMethod
import com.prismamp.todopago.model.aPaymentMethodResponse
import com.prismamp.todopago.payment.adapter.repository.model.PaymentMethodResponse
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.InvalidPaymentMethod
import com.prismamp.todopago.util.ServiceCommunication
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

object PaymentMethodsClientSpec : Spek({

    Feature("get payment method") {
        lateinit var restClient: RestClient
        lateinit var paymentMethodsClient: PaymentMethodsClient
        val accountId = "1"
        val paymentMethod = "1"
        beforeEachScenario {
            restClient = mockk()
            paymentMethodsClient = PaymentMethodsClient(restClient)
        }

        Scenario("get payment method successfully") {

            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("mock get"){
                every {
                    restClient.get(
                        url = "/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
                        clazz = PaymentMethodResponse::class.java
                    )
                } returns Right(ResponseEntity( aPaymentMethodResponse() , HttpStatus.OK))
            }

            When("get payment method executed"){
                result = runBlocking { paymentMethodsClient.getPaymentMethod(accountId, paymentMethod) }
            }

            Then("verify get"){
                verify(exactly = 1) {
                    restClient.get(
                        url = "/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
                        clazz = PaymentMethodResponse::class.java
                    )
                }
            }

            And("return successfully"){
                result.should.be.equal(Right(aPaymentMethod()))
            }

        }

        Scenario("get payment method not found") {

            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("mock get"){
                every {
                    restClient.get(
                        url = "/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
                        clazz = PaymentMethodResponse::class.java
                    )
                } returns Left(HttpClientErrorException(NOT_FOUND))
            }

            When("get payment method executed"){
                result = runBlocking { paymentMethodsClient.getPaymentMethod(accountId, paymentMethod) }
            }

            Then("verify get"){
                verify(exactly = 1) {
                    restClient.get(
                        url = "/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
                        clazz = PaymentMethodResponse::class.java
                    )
                }
            }

            And("return successfully"){
                result.should.be.equal(Left(InvalidPaymentMethod(paymentMethod)))
            }

        }

        Scenario("payment method INTERNAL_SERVER_ERROR") {

            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("mock get"){
                every {
                    restClient.get(
                        url = "/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
                        clazz = PaymentMethodResponse::class.java
                    )
                } returns Left(HttpClientErrorException(INTERNAL_SERVER_ERROR))
            }

            When("get payment method executed"){
                result = runBlocking { paymentMethodsClient.getPaymentMethod(accountId, paymentMethod) }
            }

            Then("verify get"){
                verify(exactly = 1) {
                    restClient.get(
                        url = "/private/wallet/$accountId/operation-payment-methods/$paymentMethod",
                        clazz = PaymentMethodResponse::class.java
                    )
                }
            }

            And("return successfully"){
                result.should.be.equal(Left(ServiceCommunication(APP_NAME, MS_PAYMENT_METHODS)))
            }

        }

    }


})
