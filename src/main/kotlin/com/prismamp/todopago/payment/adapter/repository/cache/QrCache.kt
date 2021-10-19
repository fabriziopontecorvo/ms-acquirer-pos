package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Option
import arrow.core.computations.option
import com.prismamp.todopago.enum.OperationType
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.domain.model.Payment
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

typealias Parameter = String

@Repository
class QrCache(
    //private val redisTemplatePayment: RedisTemplate<String, Payment>,
    private val redisTemplate: RedisTemplate<String, String>,
) {

    companion object {
        private const val KEY_PREFIX = "used-qr"
        private const val KEY_PARAM_SEPARATOR = ":"
    }

    @Value("\${redis.operation.used-qr.ttl}")
    private var ttl: Long = 1L

    suspend fun fetchPayment(payment: Payment): Option<String> =
        option {
            redisTemplate
                .opsForValue()
                .get(payment.buildKey())
                .bind()
        }

    suspend fun markQrAsUnavailable(operationToValidate: OperationToValidate, value: String) =
        option {
            redisTemplate.opsForValue()
                .set(
                    operationToValidate.buildKey(),
                    value,
                    ttl,
                    TimeUnit.SECONDS
                )
        }

    private fun Payment.buildKey() =
        KEY_PREFIX
            .addParam(establishmentInformation.terminalNumber)
            .addParam(transactionDatetime.time())
            .addParam(qrId)

    private fun OperationToValidate.buildKey() =
        KEY_PREFIX
            .addParam(terminalNumber)
            .addParam(transactionDatetime.time())
            .addParam(qrId)

    private fun Parameter.addParam(param: Parameter) = this.plus(KEY_PARAM_SEPARATOR).plus(param)

    private fun Date.time() = SimpleDateFormat("yyyyMMdd-hhmmss").format(this)




}
