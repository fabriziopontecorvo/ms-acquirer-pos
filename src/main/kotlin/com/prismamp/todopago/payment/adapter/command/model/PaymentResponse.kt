package com.prismamp.todopago.payment.adapter.command.model

import com.prismamp.todopago.payment.domain.model.PersistablePayment
import java.util.*

data class PaymentResponse(
    val id: Long,
    val accountId: Long,
    val qrId: String,
    val amount: Double,
    val installments: Int,
    val currency: String,
    val operationType: String,
    val operationStatus: String,
    val transactionDatetime: Date,
    val error: ErrorStatus?,
    val paymentMethod: PaymentMethodResponse?,
    val sellerName: String?,
    val originalAmount: Double?,
    val discountedAmount: Double?,
    val benefitCardCode: String?,
    val benefitCardDescription: String?
) {
    companion object {
        fun from(payment: PersistablePayment) =
            with(payment) {
                PaymentResponse(
                    id = id,
                    accountId = account.id,
                    qrId = qrId,
                    amount = amount,
                    installments = installments,
                    currency = currency,
                    operationType = operationType.value,
                    operationStatus = operationStatus.translatedValue,
                    transactionDatetime = transactionDatetime,
                    error = takeIf { errorCode != null && errorMessage != null }
                        ?.let { ErrorStatus(errorCode, errorMessage) },
                    paymentMethod = with(paymentMethod) {
                        PaymentMethodResponse(
                            id = id,
                            type = type.value,
                            maskedCardNumber = PaymentMethodResponse.maskCardNumber(cardNumber),
                            validThru = cardExpirationMonth.plus("/").plus(cardExpirationYear),
                            alias = alias,
                            paymentMethodId = paymentMethodId,
                            brand = PaymentMethodResponse.PaymentMethodResponseBrand(
                                id = brand.id,
                                name = brand.name,
                                logo = brand.logo
                            ),
                            bank = PaymentMethodResponse.PaymentMethodResponseBank(
                                id = bank.id,
                                name = bank.name,
                                logo = bank.logo
                            ),
                            requiresCvv = requiresCvv
                        )
                    },
                    sellerName = sellerName,
                    originalAmount = originalAmount,
                    discountedAmount = discountedAmount,
                    benefitCardCode = benefitCardCode,
                    benefitCardDescription = benefitCardDescription
                )
            }

    }

    data class ErrorStatus(
        val code: Int?,
        val reason: String?
    )
}
