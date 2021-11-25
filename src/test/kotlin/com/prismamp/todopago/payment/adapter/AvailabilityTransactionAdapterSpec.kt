package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.None
import arrow.core.Some
import com.prismamp.todopago.payment.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.dao.QrDao
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.NotFound
import com.prismamp.todopago.util.QrUSed
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object AvailabilityTransactionAdapterSpec : Spek({

    Feature("check availability") {

        lateinit var qrCache: QrCache
        lateinit var qrDao: QrDao
        lateinit var availabilityTransactionAdapter: AvailabilityTransactionAdapter

        beforeEachScenario {
            qrCache = mockk()
            qrDao = mockk()
            availabilityTransactionAdapter = AvailabilityTransactionAdapter(qrCache, qrDao)
        }

        Scenario("check if a qr is available"){

            lateinit var result: Either<ApplicationError, Operation>
            lateinit var operation: Operation

            Given("a operation"){
                operation = anOperation()
            }

            And("mock cache"){
                every {
                  runBlocking {   qrCache.fetchOperation(operation) }
                } returns None
            }

            And("mock database"){
                every {
                    runBlocking {   qrDao.findQrOperationBy(operation.buildFilters()) }
                } returns Left(NotFound("no se encontro QR"))
            }

            When("call checkAvailability"){
                with(availabilityTransactionAdapter){
                    result = runBlocking { operation.checkAvailability() }
                }
            }

            Then("qr is available"){
                result.should.be.equal(Right(operation))
            }
        }

        Scenario("check if a qr is invalid in cache"){

            lateinit var result: Either<ApplicationError, Operation>
            lateinit var operation: Operation

            Given("a operation"){
                operation = anOperation()
            }

            And("mock cache"){
                every {
                    runBlocking {   qrCache.fetchOperation(operation) }
                } returns Some("")
            }

            When("call checkAvailability"){
                with(availabilityTransactionAdapter){
                    result = runBlocking { operation.checkAvailability() }
                }
            }

            Then("qr is invalid"){
                result.should.be.equal(Left(QrUSed(operation.qrId)))
            }
        }

        Scenario("check if a qr is invalid"){

            lateinit var result: Either<ApplicationError, Operation>
            lateinit var operation: Operation

            Given("a operation"){
                operation = anOperation()
            }

            And("mock cache"){
                every {
                    runBlocking {   qrCache.fetchOperation(operation) }
                } returns None
            }

            And("mock database"){
                every {
                    runBlocking { qrDao.findQrOperationBy(operation.buildFilters()) }
                } returns Right(Unit)
            }

            When("call checkAvailability"){
                with(availabilityTransactionAdapter){
                    result = runBlocking { operation.checkAvailability() }
                }
            }

            Then("qr is invalid"){
                result.should.be.equal(Left(QrUSed(operation.qrId)))
            }
        }

    }

})

private fun Operation.buildFilters() = mapOf(
    "qr_id" to qrId,
    "amount" to amount,
    "pos_terminal_number" to establishmentInformation.terminalNumber,
    "transaction_timestamp" to transactionDatetime
)
