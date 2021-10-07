package com.prismamp.todopago.payment.adapter.repository.model

import java.util.*

data class OperationToValidate(
        val qrId: String,
        val amount: Double,
        val terminalNumber: String,
        val transactionDatetime: Date
)
