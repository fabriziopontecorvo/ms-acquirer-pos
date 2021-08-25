package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.payment.adapter.repository.cache.TransactionLockCache
import com.prismamp.todopago.payment.application.port.out.TransactionLockOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.Error
import org.springframework.stereotype.Component

@Component
class TransactionLockAdapter(
    private val transactionLockCache: TransactionLockCache,
) : TransactionLockOutputPort {

    override suspend fun Payment.lock(): Either<Error, Payment> =
        transactionLockCache.lock(this)


}
