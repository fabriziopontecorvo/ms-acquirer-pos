package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.enum.OperationStatus
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.enum.PosType
import java.time.LocalDateTime
import java.util.*

data class Payment(
    val id: Long,
    val transactionId: Long,
    val account: Account,
    val qrId: String,
    val amount: Double,
    val installments: Int,
    val currency: String,
    val operationType: OperationType,
    val operationStatus: OperationStatus,
    val transactionDatetime: LocalDateTime,
    val errorCode: Int?,
    val errorMessage: String?,
    val paymentMethod: PaymentMethod,
    val posTerminalId: String,
    val posTraceNumber: String,
    val posTicketNumber: String,
    val establishmentId: String,
    val sellerName: String,
    val recommendationCode: String?,
    val originalAmount: Double?,
    val discountedAmount: Double?,
    val benefitCardCode: String?,
    val benefitCardDescription: String?,
    val posType: PosType
) {
    companion object {
        fun from(
            id: Long,
            persistableOperation: PersistableOperation,
        ) =
            with(persistableOperation){
                Payment(
                    id = id,
                    transactionId = transactionId,
                    account = account,
                    qrId = qrId,
                    amount = amount,
                    installments = installments,
                    currency = currency,
                    operationType = operationType,
                    operationStatus = operationStatus,
                    transactionDatetime = transactionDatetime,
                    errorCode = errorCode,
                    errorMessage = errorMessage,
                    paymentMethod = paymentMethod,
                    posTerminalId = posTerminalId,
                    posTraceNumber = posTraceNumber,
                    posTicketNumber = posTicketNumber,
                    establishmentId = establishmentId,
                    sellerName = sellerName,
                    recommendationCode = recommendationCode,
                    originalAmount = originalAmount,
                    discountedAmount = discountedAmount,
                    benefitCardCode = benefitCardCode,
                    benefitCardDescription = benefitCardDescription,
                    posType = posType
                )
            }

    }
}
