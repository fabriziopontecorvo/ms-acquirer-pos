package com.prismamp.todopago.payment.adapter

import com.prismamp.todopago.payment.adapter.repository.cache.TransactionLockCache
import com.prismamp.todopago.payment.application.port.out.ReleaseOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import org.springframework.stereotype.Component

@Component
class ReleaseAdapter(
    private val transactionLockCache: TransactionLockCache
): ReleaseOutputPort {

    override suspend fun Payment.release() {
        transactionLockCache.release(this)
    }
}
