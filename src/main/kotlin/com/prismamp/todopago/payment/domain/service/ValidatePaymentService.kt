package com.prismamp.todopago.payment.domain.service

import arrow.core.Either
import arrow.core.Either.Companion.conditionally
import com.prismamp.todopago.configuration.annotation.DomainService
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.*
import com.prismamp.todopago.util.logs.CompanionLogger

@DomainService
class ValidatePaymentService {

    companion object: CompanionLogger()

    fun validateBenefitFields(benefitNumber: String?, shoppingSessionId: String?) =
        conditionally(
            test = benefitNumber != null && shoppingSessionId != null
                    || benefitNumber == null && shoppingSessionId == null,
            ifFalse = { BenefitFieldsBadRequest },
            ifTrue = { benefitNumber }
        ).log { info("validateBenefitFields: {}", it) }

    fun validateAccount(account: Account) =
        account.let {
            conditionally(
                test = it.isValid(),
                ifFalse = { InvalidAccount(it.id.toString()) },
                ifTrue = { it }
            )
        }.log { info("validateAccount: {}", it) }


    fun validatePaymentMethodInstallments(operation: Operation, paymentMethod: PaymentMethod) =
        paymentMethod.let {
            conditionally(
                test = it.isValid() && it.operation.installments == operation.installments,
                ifFalse = { NotMatchableInstallments },
                ifTrue = { it }
            )
        }.log { info("validatePaymentMethodInstallments: {}", it) }

    fun validatePaymentMethodCvv(operation: Operation, paymentMethod: PaymentMethod) =
        paymentMethod.let {
            conditionally(
                test = it.takeIf { it.requiresCvv }?.let { operation.securityCode != null } ?: true,
                ifFalse = { SecurityCodeRequired },
                ifTrue = { it }
            )
        }.log { info("validatePaymentMethodCvv: {}", it) }

    fun validateBenefit(benefit: Benefit?): Either<ApplicationError, Benefit>? =
        benefit?.let {
            conditionally(
                test = it.isValid(),
                ifFalse = { InvalidBenefit(it.id ?: "'no id present'") },
                ifTrue = { it }
            )
        }.log { info("validateBenefit: {}", it) }

}
