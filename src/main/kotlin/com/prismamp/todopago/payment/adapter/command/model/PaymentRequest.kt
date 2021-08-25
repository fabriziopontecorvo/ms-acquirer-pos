package com.prismamp.todopago.payment.adapter.command.model

import com.prismamp.todopago.payment.domain.model.Payment
import java.util.*
import javax.validation.constraints.*

data class PaymentRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 10, message = "El campo qr_id tiene que tener entre 1 y 10 digitos")
    val qrId: String,

    val accountId: Long,

    @field:Min(1, message = "El campo amount tiene que ser mayor o igual a 1")
    val amount: Double,

    @field:Min(1, message = "El campo installments tiene que ser mayor o igual que 1")
    @field:Max(99, message = "El campo installments tiene ser menor o igual a 99")
    val installments: Int,

    @field:NotBlank(message = "El campo paymentMethodId no puede estar vacío")
    val paymentMethodKey: String,

    @field:Pattern(regexp = "\\d+", message = "El campo security_code debe contener solo decimales")
    @field:Size(min = 1, max = 4, message = "El campo security_code tiene que tener entre 1 y 4 decimales")
    val securityCode: String?,

    val establishmentInformation: EstablishmentInformation,

    @field:Size(min = 1, max = 10, message = "El campo trace_number tiene que tener entre 1 y 10 caracteres")
    val traceNumber: String,

    @field:Size(min = 1, max = 6, message = "El campo ticket_number tiene que tener entre 1 y 6 caracteres")
    val ticketNumber: String,

    val transactionDatetime: Date,

    val benefitNumber: String?,

    @field:Positive(message = "El campo original_amount tiene que ser mayor a 0")
    val originalAmount: Double?,

    @field:Positive(message = "El campo discounted_amount tiene que ser mayor a 0")
    val discountedAmount: Double?,

    @field:Size(min = 1, max = 10, message = "El campo benefit_card_code tiene que tener entre 1 y 10 caracteres")
    val benefitCardCode: String?,

    @field:Size(
        min = 1,
        max = 10,
        message = "El campo benefit_card_description tiene que tener entre 1 y 10 caracteres"
    )
    val benefitCardDescription: String?,

    val shoppingSessionId: String?,

    val posType: String?,
) {

    fun toDomain() = Payment(
        qrId = qrId,
        accountId = accountId,
        amount = amount,
        installments = installments,
        paymentMethodKey = paymentMethodKey,
        securityCode = securityCode,
        establishmentInformation =
        Payment
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

    data class EstablishmentInformation(
        @field:Size(min = 1, max = 67, message = "El campo establishment_id tiene que tener entre 1 y 67 caracteres")
        val establishmentId: String,

        @field:Size(min = 1, max = 8, message = "El campo terminal_number tiene que tener entre 1 y 8 caracteres")
        val terminalNumber: String,

        val sellerName: String,
    )

}
