package com.prismamp.todopago.payment.domain.service

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.model.aBenefit
import com.prismamp.todopago.model.aPaymentMethod
import com.prismamp.todopago.model.anAccount
import com.prismamp.todopago.model.anOperation
import com.prismamp.todopago.payment.domain.model.*
import com.prismamp.todopago.util.*
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ValidatePaymentServiceSpec: Spek({
    lateinit var service: ValidatePaymentService

    Feature("validate benefit fields"){


        beforeEachScenario {
            service = ValidatePaymentService()
        }

        Scenario("successfully validation"){
            lateinit var benefitNumber: String
            lateinit var shoppingSessionId: String
            lateinit var result: Either<ApplicationError, Pair<String?, String?>>

            Given(" a benefit number and shopping session id"){
                benefitNumber = "1"
                shoppingSessionId = "1"
            }

            When("call validate benefit fields"){
                result  = service.validateBenefitFields(benefitNumber, shoppingSessionId)
            }

            Then("success"){
                result.should.be.equal(Right(Pair(benefitNumber, shoppingSessionId)))
            }

        }

        Scenario("fail validation"){
            lateinit var benefitNumber: String
            lateinit var result: Either<ApplicationError, Pair<String?, String?>>

            Given(" a benefit number"){
                benefitNumber = "1"
            }

            When("call validate benefit fields"){
                result  = service.validateBenefitFields(benefitNumber, null)
            }

            Then("success"){
                result.should.be.equal(Left(BenefitFieldsBadRequest))
            }

        }

    }

    Feature("validate account"){


        beforeEachScenario {
            service = ValidatePaymentService()
        }

        Scenario("validate account"){

            lateinit var account: Account
            lateinit var result: Either<ApplicationError, Account>

            Given("an account"){
                account = anAccount()
            }

            When("call validate account"){
                result = service.validateAccount(account)
            }

            Then("valid account"){
                result.should.be.equal(Right(account))
            }

        }

        Scenario("invalid account"){

            lateinit var account: Account
            lateinit var result: Either<ApplicationError, Account>

            Given("an account"){
                account = anAccount().copy(id = 0)
            }

            When("call validate account"){
                result = service.validateAccount(account)
            }

            Then("invalid account"){
                result.should.be.equal(Left(InvalidAccount(account.id.toString())))
            }

        }

    }

    Feature("validate payment methods installment"){
        beforeEachScenario {
            service = ValidatePaymentService()
        }

        Scenario("valid payment method "){

            lateinit var operation: Operation
            lateinit var paymentMethod: PaymentMethod
            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("an operation and payment method"){
                operation = anOperation()
                paymentMethod = aPaymentMethod()
            }

            When("call validate payment method"){
                result = service.validatePaymentMethodInstallments(operation, paymentMethod)
            }

            Then("valid payment method"){
                result.should.be.equal(Right(paymentMethod))
            }

        }

        Scenario("invalid payment method "){

            lateinit var operation: Operation
            lateinit var paymentMethod: PaymentMethod
            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("an operation and payment method"){
                operation = anOperation().copy(installments = 2)
                paymentMethod = aPaymentMethod()
            }

            When("call validate payment method"){
                result = service.validatePaymentMethodInstallments(operation, paymentMethod)
            }

            Then("invalid payment method"){
                result.should.be.equal(Left(NotMatchableInstallments))
            }

        }
    }

    Feature("validate payment methods cvv"){
        beforeEachScenario {
            service = ValidatePaymentService()
        }

        Scenario("valid payment method "){

            lateinit var operation: Operation
            lateinit var paymentMethod: PaymentMethod
            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("an operation and payment method"){
                operation = anOperation()
                paymentMethod = aPaymentMethod()
            }

            When("call validate payment method"){
                result = service.validatePaymentMethodCvv(operation, paymentMethod)
            }

            Then("valid payment method"){
                result.should.be.equal(Right(paymentMethod))
            }

        }

        Scenario("invalid payment method "){

            lateinit var operation: Operation
            lateinit var paymentMethod: PaymentMethod
            lateinit var result: Either<ApplicationError, PaymentMethod>

            Given("an operation and payment method"){
                operation = anOperation()
                paymentMethod = aPaymentMethod().copy(requiresCvv = true)
            }

            When("call validate payment method"){
                result = service.validatePaymentMethodCvv(operation, paymentMethod)
            }

            Then("invalid payment method"){
                result.should.be.equal(Left(SecurityCodeRequired))
            }

        }
    }

    Feature("validate benefit"){

        beforeEachScenario {
            service = ValidatePaymentService()
        }

        Scenario("valid benefit"){

            lateinit var result: Either<ApplicationError, Benefit>
            lateinit var benefit: Benefit

            Given("a benefit"){
                benefit = aBenefit()
            }

            When("call validate benefit"){
                result = service.validateBenefit(benefit)!!
            }

            Then("validate benefit"){
                result.should.be.equal(Right(benefit))
            }
        }

        Scenario("invalid benefit"){

            lateinit var result: Either<ApplicationError, Benefit>
            lateinit var benefit: Benefit

            Given("a benefit"){
                benefit = aBenefit().copy(status = BenefitStatus.ERROR)
            }

            When("call validate benefit"){
                result = service.validateBenefit(benefit)!!
            }

            Then("invalid benefit"){
                result.should.be.equal(Left(InvalidBenefit(benefit.id!!)))
            }
        }

        Scenario("not benefit"){

            var result: Any? = null

            When("call validate benefit"){
                result = service.validateBenefit(null)
            }

            Then("no benefit"){
                result.should.be.equal(null)
            }
        }

    }

})
