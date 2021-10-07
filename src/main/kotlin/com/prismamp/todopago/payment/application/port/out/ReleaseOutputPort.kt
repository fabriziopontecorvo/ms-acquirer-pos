package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.ApplicationError

interface ReleaseOutputPort {
    fun <T> T.release(): Either<ApplicationError, Payment>
}
