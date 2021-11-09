package com.prismamp.todopago.payment.adapter.command.model

import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.payment.domain.model.Operation
import java.time.LocalDateTime
import javax.validation.constraints.*

data class OperationRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 10, message = "The qr_id field must have between 1 and 10 digits")
    val qrId: String,

    val accountId: Long,

    @field:Min(1, message = "The amount field must be greater than or equal to 1")
    val amount: Double,

    @field:Min(1, message = "The installments field must be greater than or equal to 1")
    @field:Max(99, message = "The installments field must be less than or equal to 99")
    val installments: Int,

    @field:NotBlank(message = "The payment_method_id field cannot be empty")
    val paymentMethodId: String,

    @field:Pattern(regexp = "\\d+", message = "The security_code field must contain only decimal numbers")
    @field:Size(min = 1, max = 4, message = "The security_code field must have between 1 and 4 numbers")
    val securityCode: String?,

    @field:Size(min = 1, max = 67, message = "The establishment_id field must be between 1 and 67 characters")
    val establishmentId: String,

    @field:Size(min = 1, max = 8, message = "The terminal_number field must be between 1 and 8 characters long")
    val terminalNumber: String,

    val sellerName: String,

    @field:Size(min = 1, max = 10, message = "The trace_number field must be between 1 and 10 characters long")
    val traceNumber: String,

    @field:Size(min = 1, max = 6, message = "The ticket_number field must be between 1 and 6 characters long")
    val ticketNumber: String,

    val transactionDatetime: LocalDateTime,

    val benefitNumber: String?,

    @field:Positive(message = "The original_amount field must be greater than 0")
    val originalAmount: Double?,

    @field:Positive(message = "The discounted_amount field must be greater than 0")
    val discountedAmount: Double?,

    @field:Size(min = 1, max = 10, message = "The benefit_card_code field must be between 1 and 10 characters")
    val benefitCardCode: String?,

    @field:Size(
        min = 1,
        max = 10,
        message = "The benefit_card_description field must be between 1 and 10 characters"
    )
    val benefitCardDescription: String?,

    val shoppingSessionId: String?,

    @field:Pattern(regexp = "^(com.adq|com.pp)\$", message = "The pos_type field must contain only com.adq or com.pp")
    val posType: String,
) {

    fun toDomain() = Operation(
        qrId = qrId,
        accountId = accountId,
        amount = amount,
        installments = installments,
        paymentMethodKey = paymentMethodId,
        securityCode = securityCode,
        establishmentInformation =
        Operation
            .EstablishmentInformation(
                establishmentId = establishmentId,
                terminalNumber = terminalNumber,
                sellerName = sellerName
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
        posType = PosType.from(posType, traceNumber, ticketNumber)
    )

}
