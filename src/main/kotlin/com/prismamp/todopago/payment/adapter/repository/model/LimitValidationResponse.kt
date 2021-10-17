package com.prismamp.todopago.payment.adapter.repository.model

import java.math.BigDecimal

data class LimitValidationResponse(
    val warnings: List<LimitReport>?,
    val rejections: List<LimitReport>?,
    val dailyAmount: BigDecimal?,
    val thirtyDaysAmount: BigDecimal?,
    val dailyTransactions: Long?,
    val thirtyDaysTransactions: Long?,
    val status: String
)
