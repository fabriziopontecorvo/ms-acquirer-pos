package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.enum.Channel
import com.prismamp.todopago.model.aBenefit
import com.prismamp.todopago.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.model.CheckBenefitRequest
import com.prismamp.todopago.payment.adapter.repository.rest.BenefitCheckClient
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.CheckBenefitError
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object BenefitAdapterSpec: Spek({

        Feature("check benefit"){

            lateinit var benefitCheckClient: BenefitCheckClient
            lateinit var adapter: BenefitAdapter

            beforeEachScenario {
                benefitCheckClient = mockk()
                adapter = BenefitAdapter(benefitCheckClient)
            }

            Scenario("check benefit"){

                lateinit var result: Either<ApplicationError, Benefit?>
                lateinit var operation: Operation
                lateinit var benefit: Benefit

                Given("an operation"){
                    operation = anOperation()
                }

                And("a benefit"){
                    benefit = aBenefit()
                }

                And("mock client"){
                    every {
                        runBlocking { benefitCheckClient.check(operation.benefitNumber, operation.buildRequest() ) }
                    } returns Right(benefit)
                }

                When("call check benefit"){
                    with(adapter){
                        result = runBlocking { operation.checkBenefit() }
                    }
                }

                Then("Benefit successfully"){
                    result.should.be.equal(Right(aBenefit()))
                }

            }

            Scenario("check benefit failure"){

                lateinit var result: Either<ApplicationError, Benefit?>
                lateinit var operation: Operation

                Given("an operation"){
                    operation = anOperation()
                }

                And("mock client"){
                    every {
                        runBlocking { benefitCheckClient.check(operation.benefitNumber, operation.buildRequest() ) }
                    } returns Left(CheckBenefitError(operation.benefitNumber!!))
                }

                When("call check benefit"){
                    with(adapter){
                        result = runBlocking { operation.checkBenefit() }
                    }
                }

                Then("Benefit error"){
                    result.should.be.equal(Left(CheckBenefitError(operation.benefitNumber!!)))
                }

            }

        }

})

private fun Operation.buildRequest() = CheckBenefitRequest(
    installments = installments,
    amount = amount,
    originalAmount = originalAmount,
    discountedAmount = discountedAmount,
    benefitCardCode = benefitCardCode,
    benefitCardDescription = benefitCardDescription,
    shoppingSessionId = shoppingSessionId,
    operationType = Channel.QRADQ
)
