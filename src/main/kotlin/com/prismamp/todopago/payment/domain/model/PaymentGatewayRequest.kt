package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.payment.application.usecase.ValidatableOperation
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class GatewayRequest(
    val qrId: String,
    val posType: String?,
    val establishmentId: String,
    val transactionDatetime: LocalDateTime,
    val paymentMethodId: Long,
    val cardData: DecidirRequestCard,
    val amount: Double,
    val currency: String,
    val installments: Int,
    val terminalData: DecidirRequestTerminalData,
    val benefitsData: BenefitsData?,
) {

    companion object {
        fun from(validatableOperation: ValidatableOperation) =
            with(validatableOperation) {
                GatewayRequest(
                    qrId = first.qrId,
                    posType = first.posType.value,
                    establishmentId = first.establishmentInformation.establishmentId,
                    transactionDatetime = first.transactionDatetime,
                    paymentMethodId = third.decidirId,
                    cardData = DecidirRequestCard(
                        cardNumber = third.cardNumber,
                        cardExpirationYear = third.cardExpirationYear,
                        cardExpirationMonth = third.cardExpirationMonth,
                        securityCode = first.securityCode,
                        bankData = DecidirRequestBank(
                            id = third.bank.code.toLong(),
                            description = third.bank.name
                        )
                    ),
                    amount = first.amount,
                    currency = "ARS",
                    installments = first.installments,
                    terminalData = DecidirRequestTerminalData(
                        traceNumber = first.traceNumber,
                        ticketNumber = first.ticketNumber,
                        terminalNumber = first.establishmentInformation.terminalNumber
                    ),
                    benefitsData =
                    if (first.benefitNumber.isNullOrBlank() ||
                        (first.benefitCardCode.isNullOrBlank() && first.benefitCardDescription.isNullOrBlank())
                    ) null
                    else BenefitsData(
                        benefitsCard = BenefitsCard(
                            code = first.benefitCardCode,
                            description = first.benefitCardDescription
                        ),
                        originalAmount = first.originalAmount,
                        discountedAmount = first.discountedAmount
                    )
                )
            }
    }

    data class DecidirRequestCard(
        val cardNumber: String,
        val cardExpirationYear: String,
        val cardExpirationMonth: String,
        val securityCode: String?,
        val bankData: DecidirRequestBank
    )

    data class DecidirRequestBank(
        val id: Long,
        val description: String
    )

    data class DecidirRequestTerminalData(
        val traceNumber: String,
        val ticketNumber: String,
        val terminalNumber: String
    )

    data class BenefitsData(
        val benefitsCard: BenefitsCard?,
        val originalAmount: Double?,
        val discountedAmount: Double?
    )

    data class BenefitsCard(
        val code: String?,
        val description: String?
    )
}
