package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Either
import arrow.core.computations.option
import arrow.core.rightIfNotNull
import arrow.core.rightIfNull
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.QrUSed
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*

typealias Parameter = String

@Repository
class QrCache(
    private val redisTemplate: RedisTemplate<String, String>
) {
    @Value("\${redis.operation.lock.ttl:30}")
    var ttl: Long = 1L

    companion object {
        private const val KEY_PREFIX = "used-qr"
        private const val KEY_PARAM_SEPARATOR = ":"
    }

    suspend fun checkAvailability(payment: Payment): Either<QrUSed, Payment> =
        option {
            redisTemplate
                .opsForValue()
                .get(payment.buildKey())
                .bind()
        }
            .rightIfNull { QrUSed(payment.qrId) }
            .map { payment }

    private fun Payment.buildKey() =
        KEY_PREFIX
            .addParam(establishmentInformation.terminalNumber)
            .addParam(transactionDatetime.time())
            .addParam(qrId)

    private fun Parameter.addParam(param: Parameter) = this.plus(KEY_PARAM_SEPARATOR).plus(param)

    private fun Date.time() = SimpleDateFormat("yyyyMMdd-hhmmss").format(this)

}
