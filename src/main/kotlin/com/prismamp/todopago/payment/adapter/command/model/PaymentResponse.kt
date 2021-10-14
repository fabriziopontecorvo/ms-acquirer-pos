package com.prismamp.todopago.payment.adapter.command.model

import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PersistablePayment

data class PaymentResponse(
    val id: String = ""
){
    companion object {
        fun from(payment: PersistablePayment) = PaymentResponse(
            id = "id"
        )
    }
}
