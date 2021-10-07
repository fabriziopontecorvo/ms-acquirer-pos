package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface PaymentOutputPort {
   suspend fun GatewayRequest.executePayment(): Either<ApplicationError, GatewayResponse>
}
