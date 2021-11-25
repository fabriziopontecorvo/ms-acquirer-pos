package com.prismamp.todopago.payment.adapter

import arrow.core.Some
import com.prismamp.todopago.payment.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.cache.TransactionLockCache
import com.prismamp.todopago.payment.domain.model.Operation
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ReleaseAdapterSpec : Spek({

    lateinit var transactionLockCache: TransactionLockCache
    lateinit var releaseAdapter: ReleaseAdapter
    lateinit var operation: Operation

    Feature("release operation") {

        beforeEachScenario {
            transactionLockCache = mockk()
            releaseAdapter = ReleaseAdapter(transactionLockCache)
            operation = anOperation()
        }

        Scenario("release operation") {
            lateinit var result: Any

            Given("mock cache") {
                every { runBlocking { transactionLockCache.release(operation) } } returns Some(true)
            }

            When("call release") {
                with(releaseAdapter) {
                    result = runBlocking { operation.release() }
                }
            }

            Then("operation released"){
                result.should.be.equal(Unit)
            }

        }

    }


})
