package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Either
import arrow.core.computations.option
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.payment.adapter.repository.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LockedQr
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit.SECONDS
import com.prismamp.todopago.payment.domain.model.Operation as OperationDomain

@Repository
class TransactionLockCache(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val redisTemplate: RedisTemplate<String, Operation>
) {
    @Value("\${redis.operation.lock.ttl}")
    var ttl: Long = 30L

    companion object {
        const val KEY_PREFIX = "lock-operation"
        private const val SEPARATOR = ":"
    }

    suspend fun lock(operation: OperationDomain): Either<ApplicationError, OperationDomain> =
        option {
            redisTemplate
                .opsForValue()
                .setIfAbsent(buildKey(operation.qrId), Operation.from(operation), ttl, SECONDS)
                ?.takeIf { it }
        }
            .toEither { LockedQr(operation.qrId) }
            .map { operation }

    suspend fun release(payment: OperationDomain) =
        option {
            redisTemplate
                .opsForValue()
                .operations
                .delete(buildKey(payment.qrId))
        }

    private fun buildKey(qrId: String) =
        APP_NAME
            .plus(SEPARATOR)
            .plus(KEY_PREFIX)
            .plus(SEPARATOR)
            .plus(qrId)

}
