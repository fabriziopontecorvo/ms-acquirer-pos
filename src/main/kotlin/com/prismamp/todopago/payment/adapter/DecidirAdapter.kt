package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.payment.adapter.repository.rest.DecidirClient
import com.prismamp.todopago.payment.application.port.out.PaymentOutputPort
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class DecidirAdapter(
    private val decidirClient: DecidirClient
) : PaymentOutputPort {

    override suspend fun GatewayRequest.executePayment(): Either<ApplicationError, GatewayResponse> =
        decidirClient.executePayment(this)
}
