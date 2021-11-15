package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.enum.PaymentMethodType
import com.prismamp.todopago.payment.domain.model.Bank
import com.prismamp.todopago.payment.domain.model.Brand
import com.prismamp.todopago.payment.domain.model.PaymentMethodOperation
import com.prismamp.todopago.payment.domain.model.PaymentMethod as DomainPaymentMethod


data class PaymentMethodResponse(
    val id: Long,
    val key: String,
    val alias: String,
    val paymentMethodId: Long,
    val type: String,
    val decidirId: Long,
    val cardNumber: String,
    val validThru: String,
    val bank: PaymentMethodBankResponse,
    val brand: PaymentMethodBrandResponse,
    val requiresCvv: Boolean,
    val paymentMethodDescription: String,
    val enabled: Boolean,
    val operation: PaymentMethodOperationResponse
) {
    data class PaymentMethodBankResponse(
        val id: Long,
        val code: String,
        val name: String,
        val logo: String
    )

    data class PaymentMethodBrandResponse(
        val id: Long,
        val name: String,
        val logo: String
    )

    data class PaymentMethodOperationResponse(
        val installments: Int?,
        val operationName: String
    )

    fun toDomain() = DomainPaymentMethod(
        id = id,
        key = key,
        alias = alias,
        paymentMethodId = paymentMethodId,
        type = PaymentMethodType.from(type),
        decidirId = decidirId,
        cardNumber = cardNumber,
        cardExpirationMonth = validThru.split("/")[0],
        cardExpirationYear = validThru.split("/")[1],
        requiresCvv = requiresCvv,
        bank = Bank(
            id = bank.id,
            code = bank.code,
            name = bank.name,
            logo = bank.logo
        ),
        brand = Brand(
            id = brand.id,
            name = brand.name,
            logo = brand.logo
        ),
        description = paymentMethodDescription,
        enabled = enabled,
        operation = PaymentMethodOperation(
            installments = operation.installments,
            name = operation.operationName
        )
    )

    override fun toString() =
         "PaymentMethodResponse(id=$id," +
                 " key='$key'," +
                 " alias='$alias'," +
                 " paymentMethodId=$paymentMethodId," +
                 " type='$type'," +
                 " decidirId=$decidirId," +
                 " cardNumber='${maskCardNumber()}'," +
                 " validThru='$validThru'," +
                 " bank=$bank," +
                 " brand=$brand," +
                 " requiresCvv=$requiresCvv," +
                 " paymentMethodDescription='$paymentMethodDescription'," +
                 " enabled=$enabled," +
                 " operation=$operation)"


    private fun maskCardNumber() =
         cardNumber.replaceRange(4, 12, "X".repeat(8))

}


