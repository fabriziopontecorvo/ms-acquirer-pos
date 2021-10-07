package com.prismamp.todopago.payment.adapter.repository.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.prismamp.todopago.enum.PaymentMethodType
import com.prismamp.todopago.paymentMethod.Bank
import com.prismamp.todopago.paymentMethod.Brand
import com.prismamp.todopago.payment.domain.model.PaymentMethod as DomainPaymentMethod

data class PaymentMethod(
    val id: Long = -1,
    val key: String = "",
    val alias: String = "",
    val paymentMethodId: Long = -1,
    val type: PaymentMethodType = PaymentMethodType.INVALID,
    val cardNumber: String = "111111111111",
    val cardExpirationMonth: String = "",
    val cardExpirationYear: String = "",
    val bank: Bank = Bank(),
    val brand: Brand = Brand(),
    val decidirId: Long = -1,
    val requiresCvv: Boolean = false,
    val description: String = "",
    val enabled: Boolean = true,
    val operation: PaymentMethodOperation = PaymentMethodOperation()

) {

    companion object {
        fun from(paymentMethod: DomainPaymentMethod) =
            with(paymentMethod) {
                PaymentMethod(
                    id = id,
                    key = key,
                    alias = alias,
                    paymentMethodId = paymentMethodId,
                    type = type,
                    cardNumber = cardNumber,
                    cardExpirationMonth = cardExpirationMonth,
                    cardExpirationYear = cardExpirationYear,
                    bank = bank,
                    brand = brand,
                    decidirId = decidirId,
                    requiresCvv = requiresCvv,
                    description = description,
                    enabled = enabled,
                    operation = PaymentMethodOperation(
                        installments = operation.installments,
                        name = operation.name
                    )
                )
            }
    }

    @JsonIgnore
    fun isValid() = cardNumber.isNotEmpty() && type != PaymentMethodType.INVALID

    fun maskedPaymentMethod(): PaymentMethod {
        return copy(cardNumber = maskCardNumber())
    }

    private fun maskCardNumber(): String {
        return cardNumber.replaceRange(4, 12, "X".repeat(8))
    }

    override fun toString(): String {
        return "PaymentMethod(id=$id, key='$key', alias='$alias', paymentMethodId=$paymentMethodId, type=$type, cardNumber='${maskCardNumber()}', cardExpirationMonth='$cardExpirationMonth', cardExpirationYear='$cardExpirationYear', bank=$bank, brand=$brand, decidirId=$decidirId, requiresCvv=$requiresCvv, description='$description')"
    }


}
