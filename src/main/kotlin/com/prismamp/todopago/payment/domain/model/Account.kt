package com.prismamp.todopago.payment.domain.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Account(
    val id: Long,
    val status: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val identification: String,
    val identificationType: String,
    val gender: String
) {
    companion object {
        const val STATUS_ENABLED = "CTA_HABILITADA"
        const val STATUS_DISABLED_CASH_OUT = "CTA_INHAB_CASHOUT"
        const val STATUS_VALIDATED_ON_CELLPHONE = "CTA_VALIDADA_CELULAR"
    }

    @JsonIgnore
    fun isValid() = id != 0L && (status == STATUS_ENABLED || status == STATUS_DISABLED_CASH_OUT || status == STATUS_VALIDATED_ON_CELLPHONE)
}
