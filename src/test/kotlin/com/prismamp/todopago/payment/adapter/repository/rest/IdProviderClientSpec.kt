package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_ADQUIRENTE_PERSISTENCE
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.enum.OperationType.LAPOS_PAYMENT
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.ServiceCommunication
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

object IdProviderClientSpec : Spek({

    Feature("get id") {

        lateinit var restClient: RestClient
        lateinit var idProviderClient: IdProviderClient
        lateinit var result: Either<ApplicationError, String?>

        beforeEachScenario {
            restClient = mockk()
            idProviderClient = IdProviderClient(restClient)
        }

        Scenario("get id successfully") {
            lateinit var operationType: OperationType

            Given("given a operation type") {
                operationType = LAPOS_PAYMENT
            }

            And("mock get") {
                every {
                    restClient.get(
                        url = "/private/v1/operations/id?operation=${operationType}",
                        entity = null,
                        clazz = String::class.java
                    )
                } returns Right(ResponseEntity("1", OK))
            }

            When("get id is called") {
                result = runBlocking { idProviderClient.getId(operationType) }
            }

            Then("get has executed") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "/private/v1/operations/id?operation=${operationType}",
                        entity = null,
                        clazz = String::class.java
                    )
                }
            }

            And("return a id") {
                result.should.be.equal(Right("1"))
            }

        }

        Scenario("get id fail") {
            lateinit var operationType: OperationType

            Given("given a operation type") {
                operationType = LAPOS_PAYMENT
            }

            And("mock get") {
                every {
                    restClient.get(
                        url = "/private/v1/operations/id?operation=${operationType}",
                        entity = null,
                        clazz = String::class.java
                    )
                } returns Left(HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
            }

            When("get id is called") {
                result = runBlocking { idProviderClient.getId(operationType) }
            }

            Then("get has executed") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "/private/v1/operations/id?operation=${operationType}",
                        entity = null,
                        clazz = String::class.java
                    )
                }
            }

            And("return a error") {
                result.should.be.equal(Left(ServiceCommunication(APP_NAME, MS_ADQUIRENTE_PERSISTENCE)))
            }

        }

    }

})
