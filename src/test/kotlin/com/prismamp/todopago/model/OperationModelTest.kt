package com.prismamp.todopago.model

import com.prismamp.todopago.enum.Channel
import com.prismamp.todopago.enum.OperationStatus.APPROVED
import com.prismamp.todopago.enum.OperationType.LAPOS_PAYMENT
import com.prismamp.todopago.enum.PersistenceOperationType
import com.prismamp.todopago.enum.PosType.LAPOS
import com.prismamp.todopago.enum.PosType.PAYSTORE
import com.prismamp.todopago.payment.adapter.command.model.OperationRequest
import com.prismamp.todopago.payment.adapter.repository.model.Account
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.adapter.repository.model.QueuedOperation
import com.prismamp.todopago.payment.application.usecase.ValidatableOperation
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.payment.domain.model.PersistableOperation
import java.time.LocalDateTime

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
        transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
        benefitNumber = "12341234",
        originalAmount = 200.0,
        discountedAmount = 100.0,
        benefitCardCode = "DC",
        benefitCardDescription = "clarin",
        shoppingSessionId = "shoppingSession",
        posType = PAYSTORE
    )

fun anOperationToValidate() =
    OperationToValidate(
        qrId = "1",
        amount = 100.0,
        terminalNumber = "888",
        transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
    )

fun aFiltersMap() =
    mapOf(
        "qr_id" to anOperation().qrId,
        "amount" to anOperation().amount,
        "pos_terminal_number" to anOperation().establishmentInformation.terminalNumber,
        "transaction_timestamp" to anOperation().transactionDatetime
    )

fun anOperationToPersist() =
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
            transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
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

fun aValidatableOperation() =
    ValidatableOperation(anOperation(), anAccount(), aPaymentMethod(), aBenefit())

fun aPersistableOperation() =
    PersistableOperation(
        transactionId = 1,
        account = anAccount(),
        qrId = "qrId",
        amount = 100.00,
        installments = 1,
        currency = "ARS",
        operationType = LAPOS_PAYMENT,
        operationStatus = APPROVED,
        transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
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
        discountedAmount = 10.0,
        benefitCardCode = "card",
        benefitCardDescription = "desc",
        posType = LAPOS
    )

fun aValidPersistableOperation() =
    PersistableOperation(
        transactionId = 1L,
        account = anAccount(),
        qrId = "1",
        amount = 100.0,
        installments = 1,
        currency = "ARS",
        operationType = LAPOS_PAYMENT,
        operationStatus = APPROVED,
        transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
        errorCode = 1,
        errorMessage = "",
        paymentMethod = aPaymentMethod().maskedPaymentMethod(),
        posTerminalId = "888",
        posTraceNumber = "888",
        posTicketNumber = "123",
        establishmentId = "777",
        sellerName = "Fabrizio",
        recommendationCode = "12341234",
        originalAmount = 200.0,
        discountedAmount = 100.0,
        benefitCardCode = "DC",
        benefitCardDescription = "clarin",
        posType = PAYSTORE
    )

fun aOperationRequest() =
    OperationRequest(
        qrId = "2222124",
        accountId = 1,
        amount = 100.00,
        installments = 1,
        paymentMethodId = "52264ff0-e9d0-4dbe-b856-300d6b647bb9",
        securityCode = null,
        establishmentId = "establishment",
        terminalNumber = "terminal",
        sellerName = "seller",
        traceNumber = "trace",
        ticketNumber = "ticket",
        transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
        benefitNumber = "12341234",
        originalAmount = 150.00,
        discountedAmount = 50.00,
        benefitCardCode = "card",
        benefitCardDescription = "clarin",
        shoppingSessionId = "shopping",
        posType = "com.adq"
    )

val aJsonRequest =
    """ 
        
            {
              "qr_id":"2222124",
              "account_id":1,
              "amount":  100.00,
              "installments": 1,
              "payment_method_id":"52264ff0-e9d0-4dbe-b856-300d6b647bb9",
              "establishment_id": "establishment",
              "terminal_number":"terminal",
              "trace_number":"trace",
              "ticket_number":"ticket",
              "transaction_datetime": "2021-11-08T23:59:59",
              "seller_name": "seller",
              "shopping_session_id": "shopping",
              "benefit_card_description": "clarin",
              "benefit_card_code": "card",
              "original_amount": 150.00,
              "discounted_amount": 50.00,
              "benefit_number": "12341234",
              "pos_type": "com.adq"
            }
        
    """.trimIndent()
