package com.prismamp.todopago.payment.adapter.command.controller

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.commons.rest.handler.HttpErrorsControllerAdvice
import com.prismamp.todopago.model.aJsonRequest
import com.prismamp.todopago.model.aOperationRequest
import com.prismamp.todopago.model.aPayment
import com.prismamp.todopago.payment.adapter.command.model.OperationRequest
import com.prismamp.todopago.payment.adapter.command.model.PaymentResponse
import com.prismamp.todopago.payment.application.port.`in`.MakePaymentInputPort
import com.prismamp.todopago.util.BadRequest
import com.prismamp.todopago.util.jackson2HttpMessageConverter
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders

object PaymentControllerSpec : Spek({

    Feature("execute payment") {
        lateinit var controller: PaymentController
        lateinit var mockMvc: MockMvc
        lateinit var makePaymentInputPort: MakePaymentInputPort

        beforeEachScenario {
            makePaymentInputPort = mockk()
            controller = PaymentController(makePaymentInputPort)
            mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(jackson2HttpMessageConverter())
                .setControllerAdvice(HttpErrorsControllerAdvice())
                .build()
        }

        Scenario("execute a payment") {

            lateinit var result: ResultActions
            lateinit var request: OperationRequest
            lateinit var paymentResponse: PaymentResponse

            Given("a request") {
                request = aOperationRequest()
            }

            And("a response"){
                paymentResponse = PaymentResponse.from(aPayment())
            }

            And("mock make payment") {
                every {
                    runBlocking { makePaymentInputPort.execute(operation = request.toDomain()) }
                } returns Right(aPayment())
            }

            When("doing a post http call") {
                result = mockMvc.perform(
                    MockMvcRequestBuilders.post(
                        "/public/v1/payments"
                    )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequest)
                        .header("Accept-Language", "en")
                )
            }

            Then("response is ok and approved") {
                result.andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(jsonPath("$.id").value(paymentResponse.id))
                    .andExpect(jsonPath("$.account_id").value(paymentResponse.accountId))
                    .andExpect(jsonPath("$.qr_id").value(paymentResponse.qrId))
                    .andExpect(jsonPath("$.amount").value(paymentResponse.amount))
                    .andExpect(jsonPath("$.installments").value(paymentResponse.installments))
                    .andExpect(jsonPath("$.currency").value(paymentResponse.currency))
                    .andExpect(jsonPath("$.operation_type").value(paymentResponse.operationType))
                    .andExpect(jsonPath("$.operation_status").value(paymentResponse.operationStatus))
                    .andExpect(jsonPath("$.transaction_datetime").value(paymentResponse.transactionDatetime.toString()))
                    .andExpect(jsonPath("$.error.code").doesNotExist())
                    .andExpect(jsonPath("$.error.reason").doesNotExist())
                    .andExpect(jsonPath("$.seller_name").value(paymentResponse.sellerName!!))
                    .andExpect(jsonPath("$.original_amount").value(paymentResponse.originalAmount!!))
                    .andExpect(jsonPath("$.discounted_amount").value(paymentResponse.discountedAmount!!))
                    .andExpect(jsonPath("$.benefit_card_code").value(paymentResponse.benefitCardCode!!))
                    .andExpect(jsonPath("$.benefit_card_description").value(paymentResponse.benefitCardDescription!!))
                    .andExpect(jsonPath("$.payment_method.id").value(paymentResponse.paymentMethod!!.id))
                    .andExpect(jsonPath("$.payment_method.type").value(paymentResponse.paymentMethod!!.type))
                    .andExpect(jsonPath("$.payment_method.masked_card_number").value(paymentResponse.paymentMethod!!.maskedCardNumber))
                    .andExpect(jsonPath("$.payment_method.valid_thru").value(paymentResponse.paymentMethod!!.validThru))
                    .andExpect(jsonPath("$.payment_method.alias").value(paymentResponse.paymentMethod!!.alias))
                    .andExpect(jsonPath("$.payment_method.payment_method_id").value(paymentResponse.paymentMethod!!.paymentMethodId))
                    .andExpect(jsonPath("$.payment_method.requires_cvv").value(paymentResponse.paymentMethod!!.requiresCvv))
                    .andExpect(jsonPath("$.payment_method.brand.id").value(paymentResponse.paymentMethod!!.brand.id))
                    .andExpect(jsonPath("$.payment_method.brand.name").value(paymentResponse.paymentMethod!!.brand.name))
                    .andExpect(jsonPath("$.payment_method.brand.logo").value(paymentResponse.paymentMethod!!.brand.logo))
                    .andExpect(jsonPath("$.payment_method.bank.id").value(paymentResponse.paymentMethod!!.bank.id))
                    .andExpect(jsonPath("$.payment_method.bank.name").value(paymentResponse.paymentMethod!!.bank.name))
                    .andExpect(jsonPath("$.payment_method.bank.logo").value(paymentResponse.paymentMethod!!.bank.logo))
            }

        }

        Scenario("execute a payment return a error") {

            lateinit var result: ResultActions
            lateinit var request: OperationRequest

            Given("a request") {
                request = aOperationRequest()
            }


            And("mock make payment") {
                every {
                    runBlocking { makePaymentInputPort.execute(operation = request.toDomain()) }
                } returns Left(BadRequest("bad request"))
            }

            When("doing a post http call") {
                result = mockMvc.perform(
                    MockMvcRequestBuilders.post(
                        "/public/v1/payments"
                    )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(aJsonRequest)
                        .header("Accept-Language", "en")
                )
            }

            Then("response is ok and approved") {
                result.andExpect(MockMvcResultMatchers.status().isBadRequest)
            }

        }

    }

})
