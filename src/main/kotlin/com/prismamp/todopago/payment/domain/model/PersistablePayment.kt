package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.enum.OperationStatus
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.enum.PosType.INVALID
import java.util.*

data class PersistablePayment(
    val transactionId: Long = -1,
    val account: Account = Account(),
    val qrId: String = "",
    val amount: Double = 0.0,
    val installments: Int = 0,
    val currency: String = "",
    val operationType: OperationType = OperationType.INVALID,
    val operationStatus: OperationStatus = OperationStatus.INVALID,
    val transactionDatetime: Date = Date(),
    val errorCode: Int? = null,
    val errorMessage: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod(),
    val posTerminalId: String = "",
    val posTraceNumber: String = "",
    val posTicketNumber: String = "",
    val establishmentId: String = "",
    val sellerName: String = "",
    val recommendationCode: String? = null,
    val originalAmount: Double? = null,
    val discountedAmount: Double? = null,
    val benefitCardCode: String? = null,
    val benefitCardDescription: String? = null,
    val posType: PosType = INVALID
) {
    companion object {
        fun from(
            gatewayRequest:  GatewayRequest,
            gatewayResponse: GatewayResponse,
        ) =
            PersistablePayment()
    }
}
