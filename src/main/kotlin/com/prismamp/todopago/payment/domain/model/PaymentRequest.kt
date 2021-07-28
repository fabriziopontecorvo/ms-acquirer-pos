package com.prismamp.todopago.payment.domain.model

import java.util.*

data class PaymentRequest(
    val qrId: String = "",
    val accountId: Long = 0,
    val amount: Double = 0.0,
    val installments: Int = 0,
    val paymentMethodKey: String = "",
    val securityCode: String? = null,
    val establishmentInformation: EstablishmentInformation = EstablishmentInformation(),
    val traceNumber: String = "",
    val ticketNumber: String = "",
    val transactionDatetime: Date = Date(),
    val benefitNumber: String? = null,
    val originalAmount: Double? = null,
    val discountedAmount: Double? = null,
    val benefitCardCode: String? = null,
    val benefitCardDescription: String? = null,
    val shoppingSessionId: String? = null,
    val posType: String? = null,
){
    data class EstablishmentInformation(
        val establishmentId: String = "",
        val terminalNumber: String = "",
        val sellerName: String = "",
    )
}
