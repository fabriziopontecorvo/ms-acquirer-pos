package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.enum.OperationStatus
import com.prismamp.todopago.enum.OperationStatus.*
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.enum.PaymentStatusRequest
import com.prismamp.todopago.enum.PosType
import java.util.*

data class PersistablePayment(
    val id: Long,
    val transactionId: Long,
    val account: Account,
    val qrId: String,
    val amount: Double,
    val installments: Int,
    val currency: String,
    val operationType: OperationType,
    val operationStatus: OperationStatus,
    val transactionDatetime: Date,
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
            request: GatewayRequest,
            response: GatewayResponse,
            payment: Payment,
            account: Account,
            paymentMethod: PaymentMethod,
        ) =
            PersistablePayment(
                id = -1,
                transactionId = response.id,
                account = account,
                qrId = request.qrId,
                amount = request.amount,
                installments = request.installments,
                currency = request.currency,
                operationType = OperationType.LAPOS_PAYMENT,
                operationStatus = when (response.statusRequest) {
                    PaymentStatusRequest.PENDING -> PENDING
                    PaymentStatusRequest.FAILURE -> REJECTED
                    PaymentStatusRequest.SUCCESS -> APPROVED
                    PaymentStatusRequest.INVALID -> INVALID
                },
                transactionDatetime = response.transactionDatetime,
                errorCode = with(response.statusDetails.response.reason) {
                    this.id.takeUnless { GatewayResponse.DecidirResponseReason.isInvalid(this) }
                },
                errorMessage = with(response.statusDetails.response.reason) {
                    this.description.takeUnless { GatewayResponse.DecidirResponseReason.isInvalid(this) }
                },
                paymentMethod = paymentMethod,
                posTerminalId = request.terminalData.terminalNumber,
                posTraceNumber = request.terminalData.terminalNumber,
                posTicketNumber = request.terminalData.ticketNumber,
                establishmentId = request.establishmentId,
                sellerName = payment.establishmentInformation.sellerName,
                recommendationCode = payment.benefitNumber,
                originalAmount = payment.originalAmount,
                discountedAmount = payment.discountedAmount,
                benefitCardCode = payment.benefitCardCode,
                benefitCardDescription = payment.benefitCardDescription,
                posType = PosType.from(request.posType)
            )
    }
}
