package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.application.usecase.ValidatableOperation
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface LimitOutputPort {
   suspend fun ValidatableOperation.validateLimit(): Either<ApplicationError, Unit>
}
