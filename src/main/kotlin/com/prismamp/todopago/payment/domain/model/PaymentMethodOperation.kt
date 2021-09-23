package com.prismamp.todopago.payment.domain.model

data class PaymentMethodOperation(
        val installments: Int? = -1,
        val name: String = ""
)
