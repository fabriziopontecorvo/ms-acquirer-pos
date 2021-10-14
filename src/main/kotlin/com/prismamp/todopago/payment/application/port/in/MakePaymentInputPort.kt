package com.prismamp.todopago.payment.application.port.`in`

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PersistablePayment
import com.prismamp.todopago.util.ApplicationError


interface MakePaymentInputPort {
    suspend fun execute(payment: Payment): Either<ApplicationError, PersistablePayment>
}
