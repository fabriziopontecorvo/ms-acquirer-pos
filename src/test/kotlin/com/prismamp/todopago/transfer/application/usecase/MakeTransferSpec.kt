package com.prismamp.todopago.transfer.application.usecase

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.transfer.application.port.out.*
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.transfer.domain.model.Transfer
import com.prismamp.todopago.transfer.model.aTransfer
import com.prismamp.todopago.transfer.model.anOperation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LockedQr
import com.prismamp.todopago.util.UnprocessableEntity
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object MakeTransferSpec : Spek({

    Feature("Make transfer") {

        lateinit var makeTransfer: MakeTransfer
        lateinit var transferLockOutputPort: TransferLockOutputPort
        lateinit var virtualAccountOutputPort: VirtualAccountOutputPort
        lateinit var transferOutputPort: TransferOutputPort
        lateinit var transferPersistenceOutputPort: TransferPersistenceOutputPort
        lateinit var transferReleaseOutputPort: TransferReleaseOutputPort

        beforeEachScenario {
            transferLockOutputPort = mockk()
            virtualAccountOutputPort = mockk()
            transferOutputPort = mockk()
            transferPersistenceOutputPort = mockk()
            transferReleaseOutputPort = mockk()
            makeTransfer = MakeTransfer(
                transferLockOutputPort = transferLockOutputPort,
                virtualAccountOutputPort = virtualAccountOutputPort,
                transferOutputPort = transferOutputPort,
                transferPersistenceOutputPort = transferPersistenceOutputPort,
                transferReleaseOutputPort = transferReleaseOutputPort
            )
        }

        Scenario("execute a successfully payment by transfer") {
            lateinit var operation: Operation
            lateinit var transfer: Transfer
            lateinit var result: Either<ApplicationError, Transfer>

            Given("a operation") {
                operation = anOperation()
            }

            And("a transfer") {
                transfer = aTransfer()
            }

            And("mock lock") {
                every {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                } returns Right(operation)
            }

            And("mock check virtual account") {
                every {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                } returns Right(Unit)
            }

            And("mock make transfer") {
                every {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                } returns Right(transfer)
            }

            And("mock persist") {
                every {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                } returns Right(transfer)
            }

            And("mock release") {
                every {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                } returns Unit
            }

            When("execute make transfer") {
                result = runBlocking { makeTransfer.execute(operation) }
            }

            Then("verify calls") {
                verify(exactly = 1) {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                }

                verify(exactly = 1) {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                }

                verify(exactly = 1) {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                }

                verify(exactly = 1) {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                }

                verify(exactly = 1) {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                }
            }

            And("transfer was successfully"){
                result.should.be.equal(Right(transfer))
            }
        }

        Scenario("execute a failed payment by transfer when try to persist") {
            lateinit var operation: Operation
            lateinit var transfer: Transfer
            lateinit var result: Either<ApplicationError, Transfer>

            Given("a operation") {
                operation = anOperation()
            }

            And("a transfer") {
                transfer = aTransfer()
            }

            And("mock lock") {
                every {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                } returns Right(operation)
            }

            And("mock check virtual account") {
                every {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                } returns Right(Unit)
            }

            And("mock make transfer") {
                every {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                } returns Right(transfer)
            }

            And("mock persist") {
                every {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                } returns Left(UnprocessableEntity("persist fail"))
            }

            And("mock release") {
                every {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                } returns Unit
            }

            When("execute make transfer") {
                result = runBlocking { makeTransfer.execute(operation) }
            }

            Then("verify calls") {
                verify(exactly = 1) {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                }

                verify(exactly = 1) {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                }

                verify(exactly = 1) {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                }

                verify(exactly = 1) {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                }

                verify(exactly = 1) {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                }
            }

            And("transfer fail in the persistence"){
                result.should.be.equal(Left(UnprocessableEntity("persist fail")))
            }
        }

        Scenario("execute a failed payment by transfer when try to pay") {
            lateinit var operation: Operation
            lateinit var transfer: Transfer
            lateinit var result: Either<ApplicationError, Transfer>

            Given("a operation") {
                operation = anOperation()
            }

            And("a transfer") {
                transfer = aTransfer()
            }

            And("mock lock") {
                every {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                } returns Right(operation)
            }

            And("mock check virtual account") {
                every {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                } returns Right(Unit)
            }

            And("mock make transfer") {
                every {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                } returns Left(UnprocessableEntity("transfer fail"))
            }


            And("mock release") {
                every {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                } returns Unit
            }

            When("execute make transfer") {
                result = runBlocking { makeTransfer.execute(operation) }
            }

            Then("verify calls") {
                verify(exactly = 0) {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                }

                verify(exactly = 1) {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                }

                verify(exactly = 1) {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                }

                verify(exactly = 1) {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                }

                verify(exactly = 1) {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                }
            }

            And("transfer fail in the persistence"){
                result.should.be.equal(Left(UnprocessableEntity("transfer fail")))
            }
        }

        Scenario("execute a failed payment by transfer when check virtual account") {
            lateinit var operation: Operation
            lateinit var transfer: Transfer
            lateinit var result: Either<ApplicationError, Transfer>

            Given("a operation") {
                operation = anOperation()
            }

            And("a transfer") {
                transfer = aTransfer()
            }

            And("mock lock") {
                every {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                } returns Right(operation)
            }

            And("mock check virtual account") {
                every {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                } returns Left(UnprocessableEntity("check viortual account fail"))
            }

            And("mock release") {
                every {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                } returns Unit
            }

            When("execute make transfer") {
                result = runBlocking { makeTransfer.execute(operation) }
            }

            Then("verify calls") {
                verify(exactly = 0) {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                }

                verify(exactly = 1) {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                }

                verify(exactly = 0) {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                }

                verify(exactly = 1) {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                }

                verify(exactly = 1) {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                }
            }

            And("transfer fail in the persistence"){
                result.should.be.equal(Left(UnprocessableEntity("check viortual account fail")))
            }
        }

        Scenario("execute a failed payment by transfer when check virtual account") {
            lateinit var operation: Operation
            lateinit var transfer: Transfer
            lateinit var result: Either<ApplicationError, Transfer>

            Given("a operation") {
                operation = anOperation()
            }

            And("a transfer") {
                transfer = aTransfer()
            }

            And("mock lock") {
                every {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                } returns Left(LockedQr(operation.detail.qrTransactionId))
            }

            And("mock release") {
                every {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                } returns Unit
            }

            When("execute make transfer") {
                result = runBlocking { makeTransfer.execute(operation) }
            }

            Then("verify calls") {
                verify(exactly = 0) {
                    with(transferPersistenceOutputPort) {
                        runBlocking { transfer.persist() }
                    }
                }

                verify(exactly = 1) {
                    with(transferReleaseOutputPort) {
                        runBlocking { operation.release() }
                    }
                }

                verify(exactly = 0) {
                    with(transferOutputPort) {
                        runBlocking { operation.makeTransfer() }
                    }
                }

                verify(exactly = 0) {
                    with(virtualAccountOutputPort) {
                        runBlocking { operation.checkVirtualAccount() }
                    }
                }

                verify(exactly = 1) {
                    with(transferLockOutputPort) {
                        runBlocking { operation.lock() }
                    }
                }
            }

            And("transfer fail in the persistence"){
                result.should.be.equal(Left(LockedQr(operation.detail.qrTransactionId)))
            }
        }

    }

})
