package com.prismamp.todopago.payment.domain.service

import arrow.core.Either
import arrow.core.Either.Companion.conditionally
import com.prismamp.todopago.configuration.annotation.DomainService
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.*

@DomainService
class ValidatePaymentService {

    fun validateAccount(account: Account): Either<ApplicationError, Account> =
        account
            .let {
                conditionally(
                    test = it.isValid(),
                    ifFalse = { InvalidAccount(it.id.toString()) },
                    ifTrue = { it }
                )
            }

    fun validatePaymentMethodInstallments(operation: Operation, paymentMethod: PaymentMethod): Either<ApplicationError, PaymentMethod> =
        paymentMethod
            .let {
                conditionally(
                    test = it.isValid() && it.operation.installments == operation.installments,
                    ifFalse = { NotMatchableInstallments },
                    ifTrue = { it }
                )
            }

    fun validatePaymentMethodCvv(operation: Operation, paymentMethod: PaymentMethod): Either<ApplicationError, PaymentMethod> =
        paymentMethod
            .let {
                conditionally(
                    test = it.takeIf { it.requiresCvv }?.let { operation.securityCode != null } ?: true ,
                    ifFalse = { SecurityCodeRequired },
                    ifTrue = { it }
                )
            }

    fun validateBenefit(benefit: Benefit?): Either<ApplicationError, Benefit>? =
        benefit
            ?.let {
                conditionally(
                    test = it.isValid(),
                    ifFalse = { InvalidBenefit(it.id ?: "'no id present'") },
                    ifTrue = { it }
                )
            }
}
