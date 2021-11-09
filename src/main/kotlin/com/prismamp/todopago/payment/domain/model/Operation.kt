package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.enum.PosType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Operation(
    val qrId: String,
    val accountId: Long,
    val amount: Double,
    val installments: Int,
    val paymentMethodKey: String,
    val securityCode: String?,
    val establishmentInformation: EstablishmentInformation,
    val traceNumber: String,
    val ticketNumber: String,
    val transactionDatetime: LocalDateTime,
    val benefitNumber: String?,
    val originalAmount: Double?,
    val discountedAmount: Double?,
    val benefitCardCode: String?,
    val benefitCardDescription: String?,
    val shoppingSessionId: String?,
    val posType: PosType
){
    data class EstablishmentInformation(
        val establishmentId: String,
        val terminalNumber: String,
        val sellerName: String,
    )
}
