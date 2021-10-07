package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.enum.PersistenceOperationType

data class OperationToPersist(
        val queuedOperation: QueuedOperation,
        val persistenceOperationType: PersistenceOperationType
) : WithKey {
    override fun key() = queuedOperation.account!!.id.toString()
}
