package com.prismamp.todopago.model

import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationRequest
import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationResponse
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

fun aLimitValidationRequest() =
    LimitValidationRequest(
        amount = "100",
        buyerPaymentMethodId = 1,
        genre = "male",
        identificationNumber = "number",
        identificationTypeId = 1,
        channel = 1
    )

fun aLimitValidationResponse() =
    LimitValidationResponse(
        warnings = null,
        rejections = null,
        dailyAmount = null,
        thirtyDaysAmount = null,
        dailyTransactions = null,
        thirtyDaysTransactions = null,
        status = "OK"
    )
