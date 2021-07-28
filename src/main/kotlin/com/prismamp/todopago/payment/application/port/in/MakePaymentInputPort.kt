package com.prismamp.todopago.payment.application.port.`in`

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PaymentRequest
import com.prismamp.todopago.util.Error


interface MakePaymentInputPort {
    fun execute(paymentRequest: PaymentRequest): Either<Error, Payment>
}