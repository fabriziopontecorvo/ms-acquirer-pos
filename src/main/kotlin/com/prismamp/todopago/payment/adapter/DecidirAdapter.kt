package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.enum.PaymentStatusRequest.FAILURE
import com.prismamp.todopago.payment.adapter.repository.dao.DecidirErrorConverter
import com.prismamp.todopago.payment.adapter.repository.rest.DecidirClient
import com.prismamp.todopago.payment.application.port.out.PaymentOutputPort
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class DecidirAdapter(
    private val decidirClient: DecidirClient,
    private val decidirErrorConverter: DecidirErrorConverter
) : PaymentOutputPort {

    override suspend fun GatewayRequest.executePayment(): Either<ApplicationError, GatewayResponse> =
        decidirClient
            .executePayment(this)
            .map {
                it.takeIf { it.statusRequest == FAILURE }
                    ?.convertErrors()
                    ?: it
            }

    private fun GatewayResponse.convertErrors() =
        copy(statusDetails = statusDetails.mapErrors())

    private fun GatewayResponse.DecidirResponseStatusDetails.mapErrors() =
        copy(response = response.copy(reason = decidirErrorConverter.convert(response.reason)))

}
