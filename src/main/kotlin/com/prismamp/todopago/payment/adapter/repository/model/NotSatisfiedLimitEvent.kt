package com.prismamp.todopago.payment.adapter.repository.model

import java.math.BigDecimal

data class NotSatisfiedLimitEvent(
    val notificationId: Long,
    val amount: Double,
    val identification: String,
    val accountId: Long,
    val bankId: Long,
    val commerceName: String,
    val dailyAmount: BigDecimal,
    val monthlyAmount: BigDecimal,
    val dailyTransactions: Long,
    val monthlyTransactions: Long
) : WithKey {
    override fun key(): String  =
        accountId.toString()
}
