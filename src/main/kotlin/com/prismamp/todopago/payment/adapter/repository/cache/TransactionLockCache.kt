package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Either
import arrow.core.computations.option
import com.prismamp.todopago.payment.adapter.repository.model.Payment
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LockedQr
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit
import com.prismamp.todopago.payment.domain.model.Payment as PaymentDomain

@Repository
class TransactionLockCache(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val redisTemplate: RedisTemplate<String, Payment>
) {
    @Value("\${redis.operation.lock.ttl}")
    var ttl: Long = 30

    companion object {
        const val KEY_PREFIX = "lock-operation"
    }

    suspend fun lock(payment: PaymentDomain): Either<ApplicationError, PaymentDomain> =
        option {
            redisTemplate
                .opsForValue()
                .setIfAbsent(KEY_PREFIX.plus(payment.qrId).plus(payment), Payment.from(payment), ttl, TimeUnit.SECONDS)
                ?.takeIf { it }
                .bind()
        }
            .toEither { LockedQr(payment.qrId) }
            .map { payment }

}
