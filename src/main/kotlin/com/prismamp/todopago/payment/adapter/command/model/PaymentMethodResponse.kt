package com.prismamp.todopago.payment.adapter.command.model

data class PaymentMethodResponse(
    val id: Long,
    val type: String,
    val maskedCardNumber: String,
    val validThru: String,
    val alias: String,
    val paymentMethodId: Long,
    val brand: PaymentMethodResponseBrand,
    val bank: PaymentMethodResponseBank,
    val requiresCvv: Boolean
) {

    companion object {
        fun maskCardNumber(cardNumber: String) =
            cardNumber.takeIf { it.length >= 8 }
                .let { cardNumber.replaceRange(4, 12, "X".repeat(8)) }
    }

    data class PaymentMethodResponseBrand(
        val id: Long,
        val name: String,
        val logo: String
    )

    data class PaymentMethodResponseBank(
        val id: Long,
        val name: String,
        val logo: String
    )

}
