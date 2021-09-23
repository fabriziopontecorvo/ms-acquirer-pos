package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodsOutputPort {
    suspend fun Payment.getPaymentMethods(): Either<ApplicationError, PaymentMethod>
}
