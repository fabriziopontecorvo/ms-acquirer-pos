package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import arrow.core.Option
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.Error
import com.prismamp.todopago.util.LockedQr
import org.springframework.stereotype.Repository

@Repository
fun interface TransactionLockOutputPort{
    suspend fun Payment.lock(): Either<Error, Payment>
}
