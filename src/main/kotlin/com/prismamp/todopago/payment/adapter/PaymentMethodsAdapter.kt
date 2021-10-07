package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.payment.adapter.repository.rest.PaymentMethodsClient
import com.prismamp.todopago.payment.application.port.out.PaymentMethodsOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class PaymentMethodsAdapter(
    private val paymentMethodsClient: PaymentMethodsClient
) : PaymentMethodsOutputPort {

    override suspend fun Payment.getPaymentMethods(): Either<ApplicationError, PaymentMethod> =
        paymentMethodsClient.getPaymentMethod(accountId = accountId.toString(), paymentMethod = paymentMethodKey)
}
