package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.payment.domain.model.PaymentMethod


data class PaymentMethodResponse(
    val id: Long,
    val key: String,
    val alias: String,
    val paymentMethodId: Long,
    val type: String,
    val decidirId: Long,
    val cardNumber: String,
    val validThru: String,
    val bank: PaymentMethodBankResponse,
    val brand: PaymentMethodBrandResponse,
    val requiresCvv: Boolean,
    val paymentMethodDescription: String,
    val enabled: Boolean,
    val operation: PaymentMethodOperationResponse
){
    data class PaymentMethodBankResponse(
        val id: Long,
        val code: String,
        val name: String,
        val logo: String
    )

    data class PaymentMethodBrandResponse(
        val id: Long,
        val name: String,
        val logo: String
    )

    data class PaymentMethodOperationResponse(
        val installments: Int?,
        val operationName: String
    )

    fun toDomain() = PaymentMethod(

    )
}


