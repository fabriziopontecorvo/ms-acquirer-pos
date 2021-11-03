package com.prismamp.todopago.model

import com.prismamp.todopago.enum.Channel
import com.prismamp.todopago.enum.OperationStatus.APPROVED
import com.prismamp.todopago.enum.OperationType.LAPOS_PAYMENT
import com.prismamp.todopago.enum.PersistenceOperationType
import com.prismamp.todopago.enum.PosType
import com.prismamp.todopago.enum.PosType.LAPOS
import com.prismamp.todopago.payment.adapter.repository.model.Account
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.adapter.repository.model.QueuedOperation
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
        benefitNumber = "12341234",
        originalAmount = 200.0,
        discountedAmount = 100.0,
        benefitCardCode = "DC",
        benefitCardDescription = "clarin",
        shoppingSessionId = "shoppingSession",
        posType = PosType.PAYSTORE
    )

fun anOperationToValidate() =
    OperationToValidate(
        qrId = "1",
        amount = 100.0,
        terminalNumber = "888",
        transactionDatetime = Date(1635476812209)
    )

fun aFiltersMap() =
    mapOf(
        "qr_id" to anOperation().qrId,
        "amount" to anOperation().amount,
        "pos_terminal_number" to anOperation().establishmentInformation.terminalNumber,
        "transaction_timestamp" to anOperation().transactionDatetime
    )

fun anOperationToPersist()=
    OperationToPersist(
        queuedOperation = QueuedOperation(
            id = 1,
            decidirTransactionId = 1000,
            account = Account(),
            qrId = "qrId",
            channel = Channel.QRADQ.description,
            concept = "",
            amount = 100.0,
            installments = 1,
            currency = "ARS",
            operationType = LAPOS_PAYMENT,
            operationStatus = APPROVED,
            transactionDatetime = Date(1635476812209),
            errorCode = null,
            errorReason = null,
            paymentMethod = null,
            posTerminalId = "terminal",
            posTraceNumber = "trace",
            posTicketNumber = "ticket",
            establishmentId = "establishment",
            sellerName = null,
            benefit = null,
            originalAmount = null,
            discountedAmount = null,
            posType = LAPOS
        ),
        persistenceOperationType = PersistenceOperationType.SAVE
    )
