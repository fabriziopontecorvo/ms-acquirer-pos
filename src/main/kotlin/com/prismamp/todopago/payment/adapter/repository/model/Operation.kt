package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.enum.PosType
import java.time.LocalDateTime
import com.prismamp.todopago.payment.domain.model.Operation as OperationDomain

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
    val posType: PosType,
) {
    companion object {
        fun from(payment: OperationDomain) =
            with(payment) {
                Operation(
                    qrId = payment.qrId,
                    accountId = accountId,
                    amount = amount,
                    installments = installments,
                    paymentMethodKey = paymentMethodKey,
                    securityCode = securityCode,
                    establishmentInformation = with(establishmentInformation) {
                        EstablishmentInformation(
                            establishmentId = establishmentId,
                            terminalNumber = terminalNumber,
                            sellerName = sellerName
                        )
                    },
                    traceNumber = traceNumber,
                    ticketNumber = ticketNumber,
                    transactionDatetime = transactionDatetime,
                    benefitNumber = benefitNumber,
                    originalAmount = originalAmount,
                    discountedAmount = discountedAmount,
                    benefitCardCode = benefitCardCode,
                    benefitCardDescription = benefitCardDescription,
                    shoppingSessionId = shoppingSessionId,
                    posType = posType
                )
            }

    }

    data class EstablishmentInformation(
        val establishmentId: String,
        val terminalNumber: String,
        val sellerName: String,
    )

    fun toDomain() = OperationDomain(
        qrId = qrId,
        accountId = accountId,
        amount = amount,
        installments = installments,
        paymentMethodKey = paymentMethodKey,
        securityCode = securityCode,
        establishmentInformation =
        OperationDomain
            .EstablishmentInformation(
                establishmentId = establishmentInformation.establishmentId,
                terminalNumber = establishmentInformation.terminalNumber,
                sellerName = establishmentInformation.sellerName
            ),
        traceNumber = traceNumber,
        ticketNumber = ticketNumber,
        transactionDatetime = transactionDatetime,
        benefitNumber = benefitNumber,
        originalAmount = originalAmount,
        discountedAmount = discountedAmount,
        benefitCardCode = benefitCardCode,
        benefitCardDescription = benefitCardDescription,
        shoppingSessionId = shoppingSessionId,
        posType = posType
    )
}

