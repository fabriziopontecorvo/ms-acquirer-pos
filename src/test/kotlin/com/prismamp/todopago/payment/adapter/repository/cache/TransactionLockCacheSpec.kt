package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Option
import arrow.core.Some
import com.prismamp.todopago.configuration.Constants
import com.prismamp.todopago.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LockedQr
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit
import com.prismamp.todopago.payment.adapter.repository.model.Operation.Companion as OperationRepository
import com.prismamp.todopago.payment.domain.model.Operation as OperationDomain

object TransactionLockCacheSpec : Spek({

    lateinit var redisTemplate: RedisTemplate<String, Operation>
    lateinit var opsForValue: ValueOperations<String, Operation>
    lateinit var transactionLockCache: TransactionLockCache
    val keyPrefix = "lock-operation"
    val separador = ":"
    val ttl = 30L

    Feature("lock qr") {

        beforeEachScenario {
            redisTemplate = mockk()
            opsForValue = mockk()
            transactionLockCache = TransactionLockCache(redisTemplate)
        }

        Scenario("lock qr") {

            lateinit var result: Either<ApplicationError, OperationDomain>
            val operationDomain = anOperation()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue.setIfAbsent(
                        Constants.APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operationDomain.qrId,
                        OperationRepository.from(operationDomain),
                        ttl,
                        TimeUnit.SECONDS
                    )
                } returns true
            }

            When("Try to lock qr") {
                result = runBlocking {
                    transactionLockCache.lock(operationDomain)
                }
            }

            Then("operation has locked") {
                verify(exactly = 1) {
                    opsForValue.setIfAbsent(
                        Constants.APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operationDomain.qrId,
                        OperationRepository.from(operationDomain),
                        ttl,
                        TimeUnit.SECONDS
                    )
                }
            }
            And("obtained domain") {
                result.should.be.equal(Right(operationDomain))
            }

        }

        Scenario("lock qr failed") {

            lateinit var result: Either<ApplicationError, OperationDomain>
            val operationDomain = anOperation()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue.setIfAbsent(
                        Constants.APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operationDomain.qrId,
                        OperationRepository.from(operationDomain),
                        ttl,
                        TimeUnit.SECONDS
                    )
                } returns false
            }

            When("Try to lock qr") {
                result = runBlocking {
                    transactionLockCache.lock(operationDomain)
                }
            }

            Then("operation has locked") {
                verify(exactly = 1) {
                    opsForValue.setIfAbsent(
                        Constants.APP_NAME +
                                separador +
                                keyPrefix +
                                separador +
                                operationDomain.qrId,
                        OperationRepository.from(operationDomain),
                        ttl,
                        TimeUnit.SECONDS
                    )
                }
            }
            And("obtained LockedQr error") {
                result.should.be.equal(Left(LockedQr(operationDomain.qrId)))
            }

        }

        Scenario("release qr") {

            lateinit var result: Option<Boolean>
            val operationDomain = anOperation()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue
                        .operations
                        .delete(
                            Constants.APP_NAME +
                                    separador +
                                    keyPrefix +
                                    separador +
                                    operationDomain.qrId
                        )
                } returns true
            }

            When("Try to release qr") {
                result = runBlocking {
                    transactionLockCache.release(operationDomain)
                }
            }

            Then("operation has released") {
                verify(exactly = 1) {
                    opsForValue
                        .operations
                        .delete(
                            Constants.APP_NAME +
                                    separador +
                                    keyPrefix +
                                    separador +
                                    operationDomain.qrId
                        )
                }
            }
            And("release qr") {
                result.should.be.equal(Some(true))
            }

        }

        Scenario("release qr failed") {

            lateinit var result: Option<Boolean>
            val operationDomain = anOperation()

            Given("mocked ops for value") {
                every { redisTemplate.opsForValue() } returns opsForValue
            }

            And("mocked redis get") {
                every {
                    opsForValue
                        .operations
                        .delete(
                            Constants.APP_NAME +
                                    separador +
                                    keyPrefix +
                                    separador +
                                    operationDomain.qrId
                        )
                } returns false
            }

            When("Try to release qr") {
                result = runBlocking {
                    transactionLockCache.release(operationDomain)
                }
            }

            Then("operation hasn't released") {
                verify(exactly = 1) {
                    opsForValue
                        .operations
                        .delete(
                            Constants.APP_NAME +
                                    separador +
                                    keyPrefix +
                                    separador +
                                    operationDomain.qrId
                        )
                }
            }
            And("release qr failed") {
                result.should.be.equal(Some(false))
            }

        }

    }

})
