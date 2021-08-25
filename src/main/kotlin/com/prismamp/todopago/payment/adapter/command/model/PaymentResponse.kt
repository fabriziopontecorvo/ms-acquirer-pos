package com.prismamp.todopago.payment.adapter.command.model

import com.prismamp.todopago.payment.domain.model.Payment

data class PaymentResponse(
    val id: String = ""
){
    companion object {
        fun from(payment: Payment) = PaymentResponse(
            id = "id"
        )
    }
}
