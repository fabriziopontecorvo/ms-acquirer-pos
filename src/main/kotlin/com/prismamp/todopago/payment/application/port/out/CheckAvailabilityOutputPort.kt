package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.Error
import org.springframework.stereotype.Repository

@Repository
fun interface CheckAvailabilityOutputPort {
    suspend fun Payment.checkAvailability(): Either<Error, Payment>
}
