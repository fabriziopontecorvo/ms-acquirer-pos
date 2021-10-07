package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.enum.Channel
import com.prismamp.todopago.enum.OperationStatus
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.payment.domain.model.PersistablePayment
import java.util.*

data class QueuedOperation(
    val id: Long,
    val decidirTransactionId: Long,
    var account: Account?,
    val qrId: String,
    val channel: String = Channel.QRADQ.description,
    val concept: String = "",
    val amount: Double,
    val installments: Int,
    val currency: String,
    val operationType: OperationType,
    val operationStatus: OperationStatus,
    val transactionDatetime: Date,
    val errorCode: Int?,
    val errorReason: String?,
    var paymentMethod: PaymentMethod?,
    val posTerminalId: String,
    val posTraceNumber: String,
    val posTicketNumber: String,
    val establishmentId: String,
    var sellerName: String?,
    val benefit: QueuedBenefit?,
    val originalAmount: Double?,
    val discountedAmount: Double?,
    val posType: PosType
) : WithKey {

    companion object {
        fun from(persistablePayment: PersistablePayment, id: String) =
            with(persistablePayment) {
                QueuedOperation(
                    id = id.toLong(),
                    decidirTransactionId = transactionId,
                    account = Account.from(account),
                    qrId = qrId,
                    amount = amount,
                    installments = installments,
                    currency = currency,
                    operationType =  operationType,
                    operationStatus = operationStatus,
                    transactionDatetime = transactionDatetime,
                    errorCode = errorCode,
                    errorReason = errorMessage,
                    posTerminalId = posTerminalId,
                    posTraceNumber = posTraceNumber,
                    posTicketNumber = posTicketNumber,
                    establishmentId = establishmentId,
                    sellerName = sellerName,
                    originalAmount = originalAmount,
                    discountedAmount = discountedAmount,
                    posType = posType,
                    benefit = QueuedBenefit(
                        recommendationCode = recommendationCode
                    ),
                    paymentMethod = PaymentMethod.from(paymentMethod.maskedPaymentMethod())
                )
            }
    }

    override fun key() =
        account!!.id.toString()
}

data class QueuedBenefit(
    val recommendationCode: String? = null,
    val benefitId: String? = null
)
