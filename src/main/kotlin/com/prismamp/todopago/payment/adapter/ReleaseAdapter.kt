package com.prismamp.todopago.payment.adapter

import com.prismamp.todopago.payment.adapter.repository.cache.TransactionLockCache
import com.prismamp.todopago.payment.application.port.out.ReleaseOutputPort
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.stereotype.Component

@Component
class ReleaseAdapter(
    private val transactionLockCache: TransactionLockCache
): ReleaseOutputPort {

    companion object: CompanionLogger()

    override suspend fun Operation.release() {
        transactionLockCache.release(this)
            .log { info("release: qr_id: {}", qrId) }
    }
}
