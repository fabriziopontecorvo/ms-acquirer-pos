package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.enum.OperationStatus
import com.prismamp.todopago.enum.OperationStatus.*
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.enum.PaymentStatusRequest
import com.prismamp.todopago.enum.PosType
import java.util.*

data class PersistableOperation(
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
            response: GatewayResponse,
            request: GatewayRequest,
            operation: Operation,
            account: Account,
            paymentMethod: PaymentMethod,
        ) =
            PersistableOperation(
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
                    id.takeUnless { GatewayResponse.DecidirResponseReason.isInvalid(this) }
                },
                errorMessage = with(response.statusDetails.response.reason) {
                    description.takeUnless { GatewayResponse.DecidirResponseReason.isInvalid(this) }
                },
                paymentMethod = paymentMethod,
                posTerminalId = request.terminalData.terminalNumber,
                posTraceNumber = request.terminalData.terminalNumber,
                posTicketNumber = request.terminalData.ticketNumber,
                establishmentId = request.establishmentId,
                sellerName = operation.establishmentInformation.sellerName,
                recommendationCode = operation.benefitNumber,
                originalAmount = operation.originalAmount,
                discountedAmount = operation.discountedAmount,
                benefitCardCode = operation.benefitCardCode,
                benefitCardDescription = operation.benefitCardDescription,
                posType = PosType.from(request.posType)
            )
    }
}
