package com.prismamp.todopago.payment.application.usecase


import arrow.core.*
import arrow.core.computations.either
import com.prismamp.todopago.configuration.annotation.UseCase
import com.prismamp.todopago.payment.application.port.`in`.MakePaymentInputPort
import com.prismamp.todopago.payment.application.port.out.*
import com.prismamp.todopago.payment.domain.model.*
import com.prismamp.todopago.payment.domain.service.ValidatePaymentService
import com.prismamp.todopago.util.*
import com.prismamp.todopago.util.logs.CompanionLogger

typealias ValidatablePayment = Tuple4<Payment, Account, PaymentMethod, Benefit?>

@UseCase
class MakePayment(
    private val validatedPaymentService: ValidatePaymentService,
    transactionLockOutputPort: TransactionLockOutputPort,
    checkAvailabilityOutputPort: CheckAvailabilityOutputPort,
    accountOutputPort: AccountOutputPort,
    paymentMethodsOutputPort: PaymentMethodsOutputPort,
    benefitOutputPort: BenefitOutputPort,
    paymentOutputPort: PaymentOutputPort,
    persistenceOutputPort: PersistenceOutputPort,
    releaseOutputPort: ReleaseOutputPort
) : MakePaymentInputPort,
    TransactionLockOutputPort by transactionLockOutputPort,
    CheckAvailabilityOutputPort by checkAvailabilityOutputPort,
    AccountOutputPort by accountOutputPort,
    PaymentMethodsOutputPort by paymentMethodsOutputPort,
    BenefitOutputPort by benefitOutputPort,
    PaymentOutputPort by paymentOutputPort,
    PersistenceOutputPort by persistenceOutputPort,
    ReleaseOutputPort by releaseOutputPort {

    companion object : CompanionLogger()

    override suspend fun execute(payment: Payment) =
        payment
            .lock()
            .checkAvailability()
            .beforeValidation()
            .validate()
            .execute()
            .persist()
            .release()
            .log { info("execute: {}", it) }

    private suspend fun Either<ApplicationError, Payment>.checkAvailability() =
        flatMap { it.checkAvailability() }

    private suspend fun Either<ApplicationError, Payment>.beforeValidation() =
        flatMap {
            zip(
                c = it.getAccount(),
                d = it.getPaymentMethods(),
                e = it.checkBenefit(),
                map = { b, c, d, e -> ValidatablePayment(b, c, d, e) }
            )
        }

    private suspend fun Either<ApplicationError, ValidatablePayment>.validate() =
        flatMap {
            either {
                with(validatedPaymentService) {
                    validateAccount(it.second).bind()
                    validatePaymentMethodInstallments(it.first, it.third).bind()
                    validatePaymentMethodCvv(it.first, it.third).bind()
                    validateBenefit(it.fourth)?.bind()
                    GatewayRequest.from(it)
                }
            }
        }

    private suspend fun Either<ApplicationError, GatewayRequest>.execute() =
        flatMap { request ->
            request.executePayment()
                .map { it.toPersistablePayment(request) }
        }

    private fun GatewayResponse.toPersistablePayment(request: GatewayRequest) =
        PersistablePayment.from(request, this)

    private suspend fun Either<ApplicationError, PersistablePayment>.persist() =
        flatMap { it.persist() }

    private suspend fun Either<ApplicationError, Payment>.release() =
        flatMap { it.release() }

}
