package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Some
import com.prismamp.todopago.enum.PersistenceOperationType.SAVE
import com.prismamp.todopago.model.aPersistableOperation
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.kafka.PersistenceProducer
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.adapter.repository.model.QueuedOperation
import com.prismamp.todopago.payment.adapter.repository.rest.IdProviderClient
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PersistableOperation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.IdProviderFailure
import com.prismamp.todopago.util.toLocalDate
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object PersistenceAdapterSpec : Spek({

    lateinit var idProviderClient: IdProviderClient
    lateinit var persistenceProducer: PersistenceProducer
    lateinit var qrCache: QrCache
    lateinit var persistenceAdapter: PersistenceAdapter

    Feature("persist operation") {

        beforeEachScenario {
            idProviderClient = mockk()
            persistenceProducer = mockk()
            qrCache = mockk()
            persistenceAdapter = PersistenceAdapter(idProviderClient, persistenceProducer, qrCache)
        }

        Scenario("persist operation") {

            lateinit var operation: PersistableOperation
            lateinit var id: String
            lateinit var queueOperation: QueuedOperation
            lateinit var operationToPersist: OperationToPersist
            lateinit var operationToValidate: OperationToValidate
            lateinit var expectedResult: Either<ApplicationError, Payment>
            lateinit var result: Either<ApplicationError, Payment>

            Given("a persistable operation") {
                operation = aPersistableOperation()
                id = "1"
                queueOperation = QueuedOperation.from(operation, id)
                operationToPersist = OperationToPersist(queueOperation, SAVE)
                operationToValidate = OperationToValidate(
                    qrId = queueOperation.qrId,
                    amount = queueOperation.amount,
                    terminalNumber = queueOperation.posTerminalId,
                    transactionDatetime = queueOperation.transactionDatetime.toLocalDate()
                )
                expectedResult = Right( Payment.from(queueOperation.id, operation))
            }

            And("mock client") {
                every {
                    runBlocking { idProviderClient.getId(operation.operationType) }
                } returns Right(id)
            }

            And("mock producer") {
                every {
                     persistenceProducer.produce(operationToPersist)
                } returns Unit
            }

            And("mock qr cache") {
                every {
                    runBlocking { qrCache.markQrAsUnavailable(operationToValidate, "UNAVAILABLE") }
                } returns Some(Unit)
            }

            When("call persist"){
                with(persistenceAdapter){
                    result = runBlocking { operation.persist() }
                }
            }

            Then("payment response"){
                result.should.be.equal(expectedResult)
            }

        }

        Scenario("persist operation fail when try to get id ") {

            lateinit var operation: PersistableOperation
            lateinit var expectedResult: Either<ApplicationError, Payment>
            lateinit var result: Either<ApplicationError, Payment>

            Given("a persistable operation") {
                operation = aPersistableOperation()
                expectedResult = Left(IdProviderFailure)
            }

            And("mock client") {
                every {
                    runBlocking { idProviderClient.getId(operation.operationType) }
                } returns Right(null)
            }

            When("call persist"){
                with(persistenceAdapter){
                    result = runBlocking { operation.persist() }
                }
            }

            Then("payment response"){
                result.should.be.equal(expectedResult)
            }

        }

    }


})
