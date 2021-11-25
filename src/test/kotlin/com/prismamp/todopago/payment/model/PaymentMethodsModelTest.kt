package com.prismamp.todopago.payment.model

import com.prismamp.todopago.enum.PaymentMethodType.DEBIT
import com.prismamp.todopago.payment.adapter.repository.model.PaymentMethodResponse
import com.prismamp.todopago.payment.domain.model.PaymentMethod
import com.prismamp.todopago.payment.domain.model.PaymentMethodOperation
import com.prismamp.todopago.payment.domain.model.Bank
import com.prismamp.todopago.payment.domain.model.Brand

fun aPaymentMethodResponse() =
    PaymentMethodResponse(
        id = 1,
        key = "key",
        alias = "alias",
        paymentMethodId = 1,
        type = "DEBITO",
        decidirId = 1,
        cardNumber = "1234123456785678",
        validThru = "12/21",
        bank = PaymentMethodResponse.PaymentMethodBankResponse(
            id = 1,
            code = "071",
            name = "name",
            logo = "logo"
        ),
        brand = PaymentMethodResponse.PaymentMethodBrandResponse(
            id = 1,
            name = "name",
            logo = "logo"
        ),
        requiresCvv = false,
        paymentMethodDescription = "description",
        enabled = true,
        operation = PaymentMethodResponse.PaymentMethodOperationResponse(
            installments = 1,
            operationName = "name"
        )
    )

fun aPaymentMethod() =
    PaymentMethod(
        id = 1,
        key = "key",
        alias = "alias",
        paymentMethodId = 1,
        type = DEBIT,
        decidirId = 1,
        cardNumber = "1234123456785678",
        cardExpirationMonth = "12",
        cardExpirationYear = "21",
        bank = Bank(
            id = 1,
            code = "071",
            name = "name",
            logo = "logo"
        ),
        brand = Brand(
            id = 1,
            name = "name",
            logo = "logo"
        ),
        requiresCvv = false,
        description = "description",
        enabled = true,
        operation = PaymentMethodOperation(
            installments = 1,
            name = "name"
        )
    )
