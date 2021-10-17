package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.application.usecase.ValidatablePayment
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface LimitOutputPort {
   suspend fun ValidatablePayment.validateLimit(): Either<ApplicationError, Unit>
}
