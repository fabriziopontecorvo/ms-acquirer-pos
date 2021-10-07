package com.prismamp.todopago.payment.adapter.repository.model

data class PaymentMethodOperation(
        val installments: Int? = -1,
        val name: String = ""
)
