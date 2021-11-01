package com.prismamp.todopago.model

import com.prismamp.todopago.payment.adapter.repository.model.NotSatisfiedLimitEvent
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN

fun aNotSatisfiedLimitEvent() =
    NotSatisfiedLimitEvent(
        notificationId = 1,
        amount = 1.0,
        identification = "identification",
        accountId = 1,
        bankId = 1,
        commerceName = "commerce",
        dailyAmount = TEN,
        monthlyAmount = ONE,
        dailyTransactions = 1,
        monthlyTransactions = 1
    )
