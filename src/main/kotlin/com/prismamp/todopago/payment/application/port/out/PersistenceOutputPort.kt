package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.PersistablePayment
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface PersistenceOutputPort {
    suspend fun PersistablePayment.persist(): Either<ApplicationError, PersistablePayment>
}
