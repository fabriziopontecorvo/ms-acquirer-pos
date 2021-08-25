package com.prismamp.todopago.payment.application.usecase


import arrow.core.Either
import arrow.core.flatMap
import com.prismamp.todopago.configuration.annotation.UseCase
import com.prismamp.todopago.payment.application.port.`in`.MakePaymentInputPort
import com.prismamp.todopago.payment.application.port.out.CheckAvailabilityOutputPort
import com.prismamp.todopago.payment.application.port.out.TransactionLockOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.Error

@UseCase
class MakePayment(
    transactionLockOutputPort: TransactionLockOutputPort,
    checkAvailabilityOutputPort: CheckAvailabilityOutputPort
) : MakePaymentInputPort,
    TransactionLockOutputPort by transactionLockOutputPort,
    CheckAvailabilityOutputPort by checkAvailabilityOutputPort{

    override suspend fun execute(payment: Payment): Either<Error, Payment> =
        payment
            .lock()
            .flatMap { it.checkAvailability() }


}
