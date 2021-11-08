package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.cache.TransactionLockCache
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LockedQr
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object TransactionLockAdapterSpec : Spek({

    lateinit var transactionLockCache: TransactionLockCache
    lateinit var transactionLockAdapter: TransactionLockAdapter

    Feature("lock operation"){

        beforeEachScenario {
            transactionLockCache = mockk()
            transactionLockAdapter = TransactionLockAdapter(transactionLockCache)
        }

        Scenario("lock operation"){

            lateinit var operation: Operation
            lateinit var result: Either<ApplicationError, Operation>

            Given("a operation"){
                operation = anOperation()
            }

            And("mock cache"){
                every { runBlocking { transactionLockCache.lock(operation) } } returns Right(operation)
            }

            When("call lock"){
                with(transactionLockAdapter){
                    result = runBlocking { operation.lock() }
                }
            }

            Then("operation blocked succesfully"){
                result.should.be.equal(Right(operation))
            }
        }

        Scenario("lock operation fail"){

            lateinit var operation: Operation
            lateinit var result: Either<ApplicationError, Operation>

            Given("a operation"){
                operation = anOperation()
            }

            And("mock cache"){
                every { runBlocking { transactionLockCache.lock(operation) } } returns Left(LockedQr(operation.qrId))
            }

            When("call lock"){
                with(transactionLockAdapter){
                    result = runBlocking { operation.lock() }
                }
            }

            Then("operation was blocked"){
                result.should.be.equal(Left(LockedQr(operation.qrId)))
            }
        }

    }


})
