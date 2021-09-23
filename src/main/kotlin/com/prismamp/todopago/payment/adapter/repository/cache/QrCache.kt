package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Option
import arrow.core.computations.option
import com.prismamp.todopago.payment.domain.model.Payment
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*

typealias Parameter = String

@Repository
class QrCache(
    private val redisTemplate: RedisTemplate<String, Payment>
) {

    companion object {
        private const val KEY_PREFIX = "used-qr"
        private const val KEY_PARAM_SEPARATOR = ":"
    }

    suspend fun fetchPayment(payment: Payment): Option<Payment> =
        option {
            redisTemplate
                .opsForValue()
                .get(payment.buildKey())
                .bind()
        }

    private fun Payment.buildKey() =
        KEY_PREFIX
            .addParam(establishmentInformation.terminalNumber)
            .addParam(transactionDatetime.time())
            .addParam(qrId)

    private fun Parameter.addParam(param: Parameter) = this.plus(KEY_PARAM_SEPARATOR).plus(param)

    private fun Date.time() = SimpleDateFormat("yyyyMMdd-hhmmss").format(this)

}
