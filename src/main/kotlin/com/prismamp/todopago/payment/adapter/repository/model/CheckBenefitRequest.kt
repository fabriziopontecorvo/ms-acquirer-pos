package com.prismamp.todopago.payment.adapter.repository.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.prismamp.todopago.enum.Channel

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CheckBenefitRequest(
    val installments: Int,
    val operationType: Channel,
    val amount: Double?,
    val originalAmount: Double?,
    val discountedAmount: Double?,
    val benefitCardDescription: String?,
    val benefitCardCode: String?,
    val shoppingSessionId: String?,

) {

    fun queryParamsToString() =
        StringBuffer("?")
            .apply {
                installments.let { this.append("&installment=").append(installments.toString()) }
                operationType.let { this.append("&operation_type=").append(operationType.toString()) }
                amount?.let { this.append("&amount=").append(amount.toString()) }
                originalAmount?.let { this.append("&original_amount=").append(originalAmount.toString()) }
                discountedAmount?.let { this.append("&discounted_amount=").append(discountedAmount.toString()) }
                benefitCardCode?.let { this.append("&benefit_card_code=").append(benefitCardCode.toString()) }
                benefitCardDescription?.let {
                    this.append("&benefit_card_description=").append(benefitCardDescription.toString())
                }
                shoppingSessionId?.let { this.append("&shopping_session_id=").append(shoppingSessionId.toString()) }
            }
            .toString()
}
