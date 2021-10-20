package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface CheckAvailabilityOutputPort {
    suspend fun Operation.checkAvailability(): Either<ApplicationError, Operation>
}
