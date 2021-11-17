package com.prismamp.todopago.payment.application.usecase

import aGatewayRequestFromValidatable
import aGatewayResponse
import arrow.core.Either
import arrow.core.Either.Right
import com.prismamp.todopago.model.*
import com.prismamp.todopago.payment.application.port.out.*
import com.prismamp.todopago.payment.domain.model.*
import com.prismamp.todopago.payment.domain.service.ValidatePaymentService
import com.prismamp.todopago.util.ApplicationError
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object MakePaymentSpec : Spek({

    Feature("make a payment") {

        lateinit var validatedPaymentService: ValidatePaymentService
        lateinit var transactionLockOutputPort: TransactionLockOutputPort
        lateinit var accountOutputPort: AccountOutputPort
        lateinit var checkAvailabilityOutputPort: CheckAvailabilityOutputPort
        lateinit var paymentMethodsOutputPort: PaymentMethodsOutputPort
        lateinit var benefitOutputPort: BenefitOutputPort
        lateinit var paymentOutputPort: PaymentOutputPort
        lateinit var limitOutputPort: LimitOutputPort
        lateinit var persistenceOutputPort: PersistenceOutputPort
        lateinit var releaseOutputPort: ReleaseOutputPort

        lateinit var makePayment: MakePayment

        beforeEachScenario {
            validatedPaymentService = mockk()
            transactionLockOutputPort = mockk()
            checkAvailabilityOutputPort = mockk()
            accountOutputPort = mockk()
            paymentMethodsOutputPort = mockk()
            benefitOutputPort = mockk()
            paymentOutputPort = mockk()
            limitOutputPort = mockk()
            persistenceOutputPort = mockk()
            releaseOutputPort = mockk()

            makePayment = MakePayment(
                validatedPaymentService = validatedPaymentService,
                transactionLockOutputPort = transactionLockOutputPort,
                checkAvailabilityOutputPort = checkAvailabilityOutputPort,
                accountOutputPort = accountOutputPort,
                paymentMethodsOutputPort = paymentMethodsOutputPort,
                benefitOutputPort = benefitOutputPort,
                paymentOutputPort = paymentOutputPort,
                limitOutputPort = limitOutputPort,
                persistenceOutputPort = persistenceOutputPort,
                releaseOutputPort = releaseOutputPort
            )
        }

        Scenario("make a payment") {

            lateinit var operation: Operation
            lateinit var validatableOperation: ValidatableOperation
            lateinit var gatewayRequest: GatewayRequest
            lateinit var gatewayResponse: GatewayResponse
            lateinit var persistableOperation: PersistableOperation
            lateinit var result: Either<ApplicationError, Payment>

            Given("a operation") {
                operation = anOperation()
                validatableOperation = aValidatableOperation()
                gatewayRequest = aGatewayRequestFromValidatable()
                gatewayResponse = aGatewayResponse()
                persistableOperation = aValidPersistableOperation()
            }

            And("mock lock") {
                with(transactionLockOutputPort) {
                    every {
                        runBlocking { operation.lock() }
                    } returns Right(operation)
                }
            }

            And("mock check availability") {
                with(checkAvailabilityOutputPort) {
                    every {
                        runBlocking {
                            operation.checkAvailability()
                        }
                    } returns Right(operation)
                }
            }

            And("mock check request") {
                every {
                    runBlocking {
                        validatedPaymentService.validateBenefitFields(
                            operation.benefitNumber,
                            operation.shoppingSessionId
                        )
                    }
                } returns Right(
                    Pair(
                        operation.benefitNumber,
                        operation.shoppingSessionId
                    )
                )
            }

            And("mock account") {
                with(accountOutputPort) {
                    every {
                        runBlocking { operation.getAccount() }
                    } returns Right(anAccount())
                }
            }

            And("mock payment methods") {
                with(paymentMethodsOutputPort) {
                    every {
                        runBlocking { operation.getPaymentMethods() }
                    } returns Right(aPaymentMethod())
                }
            }

            And("mock benefits") {
                with(benefitOutputPort) {
                    every {
                        runBlocking { operation.checkBenefit() }
                    } returns Right(aBenefit())
                }
            }

            And("mock validate account") {
                every {
                    runBlocking { validatedPaymentService.validateAccount(anAccount()) }
                } returns Right(anAccount())
            }

            And("mock validate payment methods installment") {
                every {
                    runBlocking {
                        validatedPaymentService.validatePaymentMethodInstallments(
                            anOperation(),
                            aPaymentMethod()
                        )
                    }
                } returns Right(aPaymentMethod())
            }

            And("mock validate payment methods cvv") {
                every {
                    runBlocking { validatedPaymentService.validatePaymentMethodCvv(anOperation(), aPaymentMethod()) }
                } returns Right(aPaymentMethod())
            }

            And("mock validate benefit") {
                every {
                    runBlocking { validatedPaymentService.validateBenefit(aBenefit()) }
                } returns Right(aBenefit())
            }

            And("mock validate limit") {
                with(limitOutputPort) {
                    every {
                        runBlocking { validatableOperation.validateLimit() }
                    } returns Right(Unit)
                }
            }

            And("mock execute payment") {
                with(paymentOutputPort) {
                    every {
                        runBlocking { gatewayRequest.executePayment() }
                    } returns Right(gatewayResponse)
                }
            }

            And("mock persist payment") {
                with(persistenceOutputPort) {
                    every {
                        runBlocking { persistableOperation.persist() }
                    } returns Right(aPayment())
                }
            }

            And("mock release operation") {
                with(releaseOutputPort) {
                    every {
                        runBlocking { operation.release() }
                    } returns Unit
                }
            }

            And("mock release operation") {
                with(releaseOutputPort) {
                    every {
                        runBlocking { operation.release() }
                    } returns Unit
                }
            }

            When("call execute payment") {
                result = runBlocking { makePayment.execute(operation) }
            }

            Then("payment is successfully") {
                result.should.be.equal(Right(aPayment()))
            }

        }


    }

})
