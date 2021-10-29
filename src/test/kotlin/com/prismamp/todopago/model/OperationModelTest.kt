package com.prismamp.todopago.model

import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.domain.model.Operation
import java.util.*

fun anOperation() =
    Operation(
        qrId = "1",
        accountId = 1,
        amount = 100.0,
        installments = 1,
        paymentMethodKey = "339d9c34-d601-4fa1-b140-3b60cc1671c4",
        securityCode = null,
        establishmentInformation = Operation.EstablishmentInformation(
            establishmentId = "777",
            terminalNumber = "888",
            sellerName = "Fabrizio"
        ),
        traceNumber = "123",
        ticketNumber = "123",
        transactionDatetime = Date(1635476812209),
        benefitNumber = null,
        originalAmount = null,
        discountedAmount = null,
        benefitCardCode = null,
        benefitCardDescription = null,
        shoppingSessionId = null,
        posType = PosType.PAYSTORE
    )

fun anOperationToValidate() =
    OperationToValidate(
        qrId = "1",
        amount = 100.0,
        terminalNumber = "888",
        transactionDatetime = Date(1635476812209)
    )
