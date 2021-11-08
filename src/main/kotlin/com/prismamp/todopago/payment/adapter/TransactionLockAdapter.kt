package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.payment.adapter.repository.cache.TransactionLockCache
import com.prismamp.todopago.payment.application.port.out.TransactionLockOutputPort
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class TransactionLockAdapter(
    private val transactionLockCache: TransactionLockCache,
) : TransactionLockOutputPort {

    companion object: CompanionLogger()

    override suspend fun Operation.lock(): Either<ApplicationError, Operation> =
        transactionLockCache
            .lock(this)
            .log { info("lock: operation locked {}", it) }
}
