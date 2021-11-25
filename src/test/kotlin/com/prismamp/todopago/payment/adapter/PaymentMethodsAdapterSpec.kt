package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.payment.model.aPaymentMethod
import com.prismamp.todopago.payment.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.rest.PaymentMethodsClient
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.InvalidPaymentMethod
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object PaymentMethodsAdapterSpec : Spek({

    lateinit var paymentMethodsClient: PaymentMethodsClient
    lateinit var paymentMethodsAdapter: PaymentMethodsAdapter

    Feature("get payment methods") {

        beforeEachScenario {
            paymentMethodsClient = mockk()
            paymentMethodsAdapter = PaymentMethodsAdapter(paymentMethodsClient)
        }

        Scenario("obtain payment methods successfully") {

            lateinit var operation: Operation
            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("a operation") {
                operation = anOperation()
            }

            And("mock client") {
                every {
                    runBlocking {
                        paymentMethodsClient.getPaymentMethod(
                            accountId = operation.accountId.toString(),
                            paymentMethod = operation.paymentMethodKey
                        )
                    }
                } returns Right(aPaymentMethod())
            }

            When("call get payment methods"){
                with(paymentMethodsAdapter){
                    result = runBlocking { operation.getPaymentMethods() }
                }
            }

            Then("payment methods obtained"){
                result.should.be.equal(Right(aPaymentMethod()))
            }

        }

        Scenario("obtain payment methods failed") {

            lateinit var operation: Operation
            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("a operation") {
                operation = anOperation()
            }

            And("mock client") {
                every {
                    runBlocking {
                        paymentMethodsClient.getPaymentMethod(
                            accountId = operation.accountId.toString(),
                            paymentMethod = operation.paymentMethodKey
                        )
                    }
                } returns Left(InvalidPaymentMethod(operation.paymentMethodKey))
            }

            When("call get payment methods"){
                with(paymentMethodsAdapter){
                    result = runBlocking { operation.getPaymentMethods() }
                }
            }

            Then("payment methods obtained"){
                result.should.be.equal(Left(InvalidPaymentMethod(operation.paymentMethodKey)))
            }

        }

    }


})
