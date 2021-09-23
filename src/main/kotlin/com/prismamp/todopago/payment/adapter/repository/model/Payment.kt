package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.payment.domain.model.Payment as PaymentDomain
import java.util.*

data class Payment(
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
    val posType: PosType? = null,
) {
    companion object {
        fun from(payment: PaymentDomain) =
            with(payment) {
                Payment(
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
        val establishmentId: String = "",
        val terminalNumber: String = "",
        val sellerName: String = "",
    )

    fun toDomain() = PaymentDomain(
        qrId = qrId,
        accountId = accountId,
        amount = amount,
        installments = installments,
        paymentMethodKey = paymentMethodKey,
        securityCode = securityCode,
        establishmentInformation =
        PaymentDomain
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

