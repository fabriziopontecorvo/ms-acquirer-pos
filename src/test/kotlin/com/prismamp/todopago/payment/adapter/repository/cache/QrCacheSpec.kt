package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.model.anOperation
import com.prismamp.todopago.model.anOperationToValidate
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit.SECONDS

object QrCacheSpec : Spek({

    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var opsForValue: ValueOperations<String, String>
    lateinit var qrCache: QrCache
    val value = "UNAVAILABLE"
    val keyPrefix = "used-qr"
    val separador = ":"
    val ttl = 15L

    Feature("qr cache") {


        beforeEachScenario {
            redisTemplate = mockk()
            opsForValue = mockk()
            qrCache = QrCache(redisTemplate)
        }

        Scenario("fetch operation value of qr") {

            lateinit var result: Option<String>
            val operation = anOperation()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue.get(
                        APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operation.establishmentInformation.terminalNumber +
                                separador +
                                operation.transactionDatetime.toString() +
                                separador +
                                operation.qrId
                    )
                } returns value
            }

            When("Try to get status qr") {
                result = runBlocking {
                    qrCache.fetchOperation(operation)
                }
            }

            Then("status is obtained") {
                verify(exactly = 1) {
                    opsForValue.get(
                        APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operation.establishmentInformation.terminalNumber +
                                separador +
                                operation.transactionDatetime.toString() +
                                separador +
                                operation.qrId
                    )
                }
            }

            And("obtained key") {
                result.should.be.equal(Some(value))
            }
        }

        Scenario("no fetch operation value of qr") {

            lateinit var result: Option<String>
            val operation = anOperation()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue.get(
                        APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operation.establishmentInformation.terminalNumber +
                                separador +
                                operation.transactionDatetime.toString() +
                                separador +
                                operation.qrId
                    )
                } returns null
            }

            When("Try to get status qr") {
                result = runBlocking {
                    qrCache.fetchOperation(operation)
                }
            }

            Then("status isn't obtained") {
                verify(exactly = 1) {
                    opsForValue.get(
                        APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operation.establishmentInformation.terminalNumber +
                                separador +
                                operation.transactionDatetime.toString() +
                                separador +
                                operation.qrId
                    )
                }
            }

            And("None key") {
                result.should.be.equal(None)
            }
        }

        Scenario("mark qr as unavailable") {

            lateinit var result: Option<Unit>
            val operationToValidate = anOperationToValidate()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue.set(
                        APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operationToValidate.terminalNumber +
                                separador +
                                operationToValidate.transactionDatetime.toString() +
                                separador +
                                operationToValidate.qrId,
                        value,
                        ttl,
                        SECONDS
                    )
                } returns Unit
            }

            When("Try to get status qr") {
                result = runBlocking {
                    qrCache.markQrAsUnavailable(operationToValidate, value)
                }
            }

            Then("status is obtained") {
                verify(exactly = 1) {
                    opsForValue.set(
                        APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operationToValidate.terminalNumber +
                                separador +
                                operationToValidate.transactionDatetime.toString() +
                                separador +
                                operationToValidate.qrId,
                        value,
                        ttl,
                        SECONDS
                    )
                }
            }

            And("obtained key") {
                result.should.be.equal(Some(Unit))
            }
        }

    }

})
