package com.prismamp.todopago.payment.adapter.repository.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.prismamp.todopago.enum.Channel
import com.prismamp.todopago.payment.application.usecase.ValidatableOperation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@JsonNaming(PropertyNamingStrategy::class)
data class LimitValidationRequest(
    val amount: String,
    val buyerPaymentMethodId: Long,
    val genre: String,
    val identificationNumber: String,
    val identificationTypeId: Long,
    val channel: Long
) {
    companion object {

        fun from(validatableOperation: ValidatableOperation) = LimitValidationRequest(
            amount = DecimalFormat("0.00", DecimalFormatSymbols.getInstance().apply { decimalSeparator = '.' })
                .format(validatableOperation.first.amount),
            buyerPaymentMethodId = validatableOperation.third.id,
            genre = validatableOperation.second.gender,
            identificationNumber = validatableOperation.second.identification,
            identificationTypeId = validatableOperation.second.identificationType.toLong(),
            channel = Channel.QRADQ.id
        )

    }
}
