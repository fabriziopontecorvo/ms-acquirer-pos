package com.prismamp.todopago.payment.adapter.repository.dao

import com.prismamp.todopago.MessageConverter
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.stereotype.Component

@Component
class DecidirErrorConverter(
        private val converter: MessageConverter
) {

    companion object: CompanionLogger()

    fun convert(reason: GatewayResponse.DecidirResponseReason): GatewayResponse.DecidirResponseReason = log.benchmark(
            "Se mapea el error de Decidir: $reason -> {}"
    ) {
        converter.convertDecidirMessageFrom(reason.id.toString())
                .map { GatewayResponse.DecidirResponseReason(id = it.code.toInt(), description = it.text) }
                .orElse(reason)
    }
}
