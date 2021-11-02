package com.prismamp.todopago.model

import com.prismamp.todopago.enum.Channel.QRADQ
import com.prismamp.todopago.payment.adapter.repository.model.CheckBenefitRequest

fun aCheckBenefitRequest() =
    CheckBenefitRequest(
        installments = 1,
        operationType = QRADQ,
        amount = 75.00,
        originalAmount = 100.00,
        discountedAmount = 25.00,
        benefitCardDescription = "description",
        benefitCardCode = "code",
        shoppingSessionId = "shoppingSessionId"
    )
