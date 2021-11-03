package com.prismamp.todopago.payment.adapter.repository.cache

import arrow.core.Option
import arrow.core.computations.option
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.payment.adapter.repository.model.OperationToValidate
import com.prismamp.todopago.payment.domain.model.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

typealias Parameter = String

@Repository
class QrCache(
    private val redisTemplate: RedisTemplate<String, String>
) {

    companion object {
        private const val KEY_PREFIX = "used-qr"
        private const val SEPARATOR = ":"
    }

    @Value("\${redis.operation.used-qr.ttl}")
    private var ttl: Long = 15L

    suspend fun fetchOperation(operation: Operation): Option<String> =
        option {
            redisTemplate
                .opsForValue()
                .get(operation.buildKey())
                .bind()
        }

    suspend fun markQrAsUnavailable(operationToValidate: OperationToValidate, value: String) =
        option {
            redisTemplate
                .opsForValue()
                .set(
                    operationToValidate.buildKey(),
                    value,
                    ttl,
                    TimeUnit.SECONDS
                )
        }

    private fun Operation.buildKey() =
        buildPrefix()
            .addParam(establishmentInformation.terminalNumber)
            .addParam(transactionDatetime.time())
            .addParam(qrId)

    private fun OperationToValidate.buildKey() =
        buildPrefix()
            .addParam(terminalNumber)
            .addParam(transactionDatetime.time())
            .addParam(qrId)

    private fun buildPrefix() =
        APP_NAME
            .plus(SEPARATOR)
            .plus(KEY_PREFIX)

    private fun Parameter.addParam(param: Parameter) = this.plus(SEPARATOR).plus(param)

    private fun Date.time() = SimpleDateFormat("yyyyMMdd-hhmmss").format(this)
}
