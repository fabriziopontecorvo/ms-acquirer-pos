package com.prismamp.todopago.payment.adapter.repository.model

import java.math.BigDecimal

data class LimitValidationResult(
    val limitReport: LimitReport?,
    val dailyAmount: BigDecimal,
    val thirtyDaysAmount: BigDecimal,
    val dailyTransactions: Long,
    val thirtyDaysTransactions: Long,
    val status: String
)
