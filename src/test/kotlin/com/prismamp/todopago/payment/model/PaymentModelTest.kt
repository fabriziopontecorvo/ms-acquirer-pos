package com.prismamp.todopago.payment.model

import com.prismamp.todopago.enum.OperationStatus.APPROVED
import com.prismamp.todopago.enum.OperationType.LAPOS_PAYMENT
import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.payment.domain.model.Payment
import java.time.LocalDateTime

fun aPayment() =
    Payment(
        id = 1,
        transactionId = 1,
        account = anAccount(),
        qrId = "qrId",
        amount = 100.00,
        installments = 1,
        currency = "ARS",
        operationType = LAPOS_PAYMENT,
        operationStatus = APPROVED,
        transactionDatetime = LocalDateTime.of(2021,11,8,23,59,59),
        errorCode = null,
        errorMessage = null,
        paymentMethod = aPaymentMethod(),
        posTerminalId = "terminal",
        posTraceNumber = "trace",
        posTicketNumber = "ticket",
        establishmentId = "establishment",
        sellerName = "seller",
        recommendationCode = "code",
        originalAmount = 110.00,
        discountedAmount = 10.00,
        benefitCardCode = "card",
        benefitCardDescription = "description",
        posType = PosType.LAPOS
    )
