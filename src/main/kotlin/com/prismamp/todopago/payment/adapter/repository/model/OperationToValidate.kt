package com.prismamp.todopago.payment.adapter.repository.model

import java.time.LocalDateTime

data class OperationToValidate(
        val qrId: String,
        val amount: Double,
        val terminalNumber: String,
        val transactionDatetime: LocalDateTime
)
