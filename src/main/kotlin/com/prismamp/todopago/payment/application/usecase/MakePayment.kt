package com.prismamp.todopago.payment.application.usecase


import arrow.core.Either
import com.prismamp.todopago.configuration.annotation.UseCase
import com.prismamp.todopago.payment.application.port.`in`.MakePaymentInputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PaymentRequest
import com.prismamp.todopago.util.Error

@UseCase
class MakePayment(): MakePaymentInputPort {
    override fun execute(paymentRequest: PaymentRequest): Either<Error, Payment> {
        TODO("Not yet implemented")
    }
}