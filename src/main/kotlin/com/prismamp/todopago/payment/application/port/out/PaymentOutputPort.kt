package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.ApplicationError

interface PaymentOutputPort {

    fun GatewayRequest.executePayment(): Either<ApplicationError, Payment>

}
