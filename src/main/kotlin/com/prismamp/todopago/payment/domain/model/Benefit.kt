package com.prismamp.todopago.payment.domain.model

import com.fasterxml.jackson.annotation.JsonIgnore

enum class BenefitStatus(val value: String) {

    OK("OK"),
    ERROR("ERROR"),
    INVALID("")

}

data class Benefit(
    val status: BenefitStatus,
    val id: String?
) {

    @JsonIgnore
    fun isValid() = status == BenefitStatus.OK

}
