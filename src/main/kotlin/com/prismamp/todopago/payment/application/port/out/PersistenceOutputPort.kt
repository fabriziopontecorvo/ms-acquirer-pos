package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.payment.domain.model.PersistablePayment
import com.prismamp.todopago.util.ApplicationError

interface PersistenceOutputPort {
    fun PersistablePayment.persist(): Either<ApplicationError, Payment>
}
