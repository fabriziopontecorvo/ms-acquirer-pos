package com.prismamp.todopago.payment.domain.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.prismamp.todopago.enum.PaymentMethodType

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

    @JsonIgnore
    fun isValid() = cardNumber.isNotEmpty() && type != PaymentMethodType.INVALID

    fun maskedPaymentMethod() =
         copy(cardNumber = maskCardNumber())


    private fun maskCardNumber() =
         cardNumber.replaceRange(4, 12, "X".repeat(8))


    override fun toString() =
         "PaymentMethod(id=$id," +
                 " key='$key'," +
                 " alias='$alias'," +
                 " paymentMethodId=$paymentMethodId," +
                 " type=$type," +
                 "cardNumber='${maskCardNumber()}'," +
                 " cardExpirationMonth='$cardExpirationMonth'," +
                 " cardExpirationYear='$cardExpirationYear'," +
                 " bank=$bank," +
                 " brand=$brand," +
                 " decidirId=$decidirId," +
                 " requiresCvv=$requiresCvv" +
                 "$description')"



}
