package com.prismamp.todopago.payment.application.usecase


import arrow.core.Either
import arrow.core.Tuple4
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.zip
import com.prismamp.todopago.configuration.annotation.UseCase
import com.prismamp.todopago.payment.application.port.`in`.MakePaymentInputPort
import com.prismamp.todopago.payment.application.port.out.*
import com.prismamp.todopago.payment.domain.model.*
import com.prismamp.todopago.payment.domain.service.ValidatePaymentService
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.logs.CompanionLogger

typealias ValidatableOperation = Tuple4<Operation, Account, PaymentMethod, Benefit?>
typealias ExecutableOperation = Tuple4<Operation, Account, PaymentMethod, GatewayRequest>

@UseCase
class MakePayment(
    private val validatedPaymentService: ValidatePaymentService,
    transactionLockOutputPort: TransactionLockOutputPort,
    checkAvailabilityOutputPort: CheckAvailabilityOutputPort,
    accountOutputPort: AccountOutputPort,
    paymentMethodsOutputPort: PaymentMethodsOutputPort,
    benefitOutputPort: BenefitOutputPort,
    paymentOutputPort: PaymentOutputPort,
    limitOutputPort: LimitOutputPort,
    persistenceOutputPort: PersistenceOutputPort,
    releaseOutputPort: ReleaseOutputPort
) : MakePaymentInputPort,
    TransactionLockOutputPort by transactionLockOutputPort,
    CheckAvailabilityOutputPort by checkAvailabilityOutputPort,
    AccountOutputPort by accountOutputPort,
    PaymentMethodsOutputPort by paymentMethodsOutputPort,
    BenefitOutputPort by benefitOutputPort,
    PaymentOutputPort by paymentOutputPort,
    LimitOutputPort by limitOutputPort,
    PersistenceOutputPort by persistenceOutputPort,
    ReleaseOutputPort by releaseOutputPort {

    companion object : CompanionLogger()

    override suspend fun execute(operation: Operation) =
        operation
            .lock()
            .checkRequest()
            .checkAvailability()
            .beforeValidation()
            .validate()
            .execute()
            .persist()
            .also { operation.release() }
            .log { info("execute: {}", it) }

    private suspend fun Either<ApplicationError, Operation>.checkRequest() =
        flatMap {
            either {
                validatedPaymentService.validateBenefitFields(it.benefitNumber, it.shoppingSessionId).bind()
                it
            }
        }

    private suspend fun Either<ApplicationError, Operation>.checkAvailability() =
        flatMap { it.checkAvailability() }

    private suspend fun Either<ApplicationError, Operation>.beforeValidation() =
        flatMap {
            zip(
                c = it.getAccount(),
                d = it.getPaymentMethods(),
                e = it.checkBenefit(),
                map = { b, c, d, e -> ValidatableOperation(b, c, d, e) }
            )
        }

    private suspend fun Either<ApplicationError, ValidatableOperation>.validate() =
        flatMap {
            either {
                with(validatedPaymentService) {
                    validateAccount(it.second).bind()
                    validatePaymentMethodInstallments(it.first, it.third).bind()
                    validatePaymentMethodCvv(it.first, it.third).bind()
                    validateBenefit(it.fourth)?.bind()
                    it.validateLimit().bind()
                    ExecutableOperation(it.first, it.second, it.third, GatewayRequest.from(it))
                }
            }
        }

    private suspend fun Either<ApplicationError, ExecutableOperation>.execute() =
        flatMap {
            it.fourth
                .executePayment()
                .toPersistablePayment(it.first, it.second, it.third, it.fourth)
        }

    private suspend fun Either<ApplicationError, PersistableOperation>.persist() =
        flatMap { it.persist() }

    private fun Either<ApplicationError, GatewayResponse>.toPersistablePayment(
        operation: Operation,
        account: Account,
        paymentMethod: PaymentMethod,
        request: GatewayRequest,
    ) =
        map { PersistableOperation.from(request, it, operation, account, paymentMethod) }
}
