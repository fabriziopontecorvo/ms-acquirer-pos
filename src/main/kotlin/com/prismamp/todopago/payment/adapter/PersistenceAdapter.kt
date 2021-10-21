package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.rightIfNotNull
import com.prismamp.todopago.enum.PersistenceOperationType.SAVE
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.kafka.PersistenceProducer
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.adapter.repository.model.QueuedOperation
import com.prismamp.todopago.payment.adapter.repository.rest.IdProviderClient
import com.prismamp.todopago.payment.application.port.out.PersistenceOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PersistablePayment
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.IdProviderFailure
import com.prismamp.todopago.util.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class PersistenceAdapter(
    private val idProviderClient: IdProviderClient,
    private val persistenceProducer: PersistenceProducer,
    private val qrCache: QrCache
) : PersistenceOutputPort {

    companion object : CompanionLogger() {
        const val UNAVAILABLE = "UNAVAILABLE"
    }

    override suspend fun PersistablePayment.persist(): Either<ApplicationError, Payment> =
        idProviderClient
            .getId(operationType)
            .flatMap { it.rightIfNotNull { IdProviderFailure } }
            .map { QueuedOperation.from(this, it) }
            .map {
                persistenceProducer.operationExecutedEvent(OperationToPersist(it, SAVE))
                qrCache.markQrAsUnavailable(operationToValidate(it), UNAVAILABLE)
                Payment.from(it.id, this)
            }

    private fun operationToValidate(queuedOperation: QueuedOperation) =
        OperationToValidate(
            qrId = queuedOperation.qrId,
            amount = queuedOperation.amount,
            terminalNumber = queuedOperation.posTerminalId,
            transactionDatetime = queuedOperation.transactionDatetime
        )
}
