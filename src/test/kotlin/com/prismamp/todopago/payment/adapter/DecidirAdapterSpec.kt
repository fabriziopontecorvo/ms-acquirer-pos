package com.prismamp.todopago.payment.adapter

import aGatewayRequest
import aGatewayResponse
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.DECIDIR
import com.prismamp.todopago.enum.PaymentStatusRequest
import com.prismamp.todopago.payment.adapter.repository.dao.DecidirErrorConverter
import com.prismamp.todopago.payment.adapter.repository.rest.DecidirClient
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.ServiceCommunication
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object DecidirAdapterSpec: Spek({

    Feature("execute a payment"){

        lateinit var decidirClient: DecidirClient
        lateinit var decidirErrorConverter: DecidirErrorConverter
        lateinit var decidirAdapter: DecidirAdapter

        beforeEachScenario {
            decidirClient = mockk()
            decidirErrorConverter = mockk()
            decidirAdapter = DecidirAdapter(decidirClient, decidirErrorConverter)
        }

        Scenario("execute a payment"){

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            lateinit var response: GatewayResponse

            Given("a request and response"){
                request = aGatewayRequest()
                response = aGatewayResponse()
            }

            And("mock client"){
                every {
                    runBlocking { decidirClient.executePayment(request) }
                } returns Right(response)
            }

            And("mock converter"){
                every {
                    runBlocking { decidirErrorConverter.convert(response.statusDetails.response.reason) }
                } returns aReason()
            }

            When("call execute payment"){
                with(decidirAdapter){
                    result = runBlocking { request.executePayment() }
                }
            }

            Then("return response"){
                result.should.be.equal(Right(response))
            }

        }

        Scenario("execute a payment with status failure"){

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest
            lateinit var response: GatewayResponse

            Given("a request and response"){
                request = aGatewayRequest()
                response = aGatewayResponse().copy(statusRequest = PaymentStatusRequest.FAILURE)
            }

            And("mock client"){
                every {
                    runBlocking { decidirClient.executePayment(request) }
                } returns Right(response)
            }

            And("mock converter"){
                every {
                    runBlocking { decidirErrorConverter.convert(response.statusDetails.response.reason) }
                } returns aReason()
            }

            When("call execute payment"){
                with(decidirAdapter){
                    result = runBlocking { request.executePayment() }
                }
            }

            Then("return mapped errors"){
                result.should.be.equal(Right(response.convertErrors()))
            }

        }

        Scenario("execute a payment fail"){

            lateinit var result: Either<ApplicationError, GatewayResponse>
            lateinit var request: GatewayRequest

            Given("a request and response"){
                request = aGatewayRequest()
            }

            And("mock client"){
                every {
                    runBlocking { decidirClient.executePayment(request) }
                } returns Left(ServiceCommunication(APP_NAME, DECIDIR))
            }

            When("call execute payment"){
                with(decidirAdapter){
                    result = runBlocking { request.executePayment() }
                }
            }

            Then("return a fail"){
                result.should.be.equal(Left(ServiceCommunication(APP_NAME, DECIDIR)))
            }

        }


    }

})

fun aReason() =
    GatewayResponse.DecidirResponseReason(
        id = 71,
        description = "sara",
        additionalDescription = "sa"
    )

private fun GatewayResponse.convertErrors() =
    copy(statusDetails = statusDetails.mapErrors())

private fun GatewayResponse.DecidirResponseStatusDetails.mapErrors() =
    copy(response = response.copy(reason = aReason()))
