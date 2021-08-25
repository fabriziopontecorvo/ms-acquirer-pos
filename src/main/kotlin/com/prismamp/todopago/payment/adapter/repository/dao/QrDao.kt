package com.prismamp.todopago.payment.adapter.repository.dao

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.Error
import org.springframework.stereotype.Repository

@Repository
class QrDao {

    fun checkAvailability(payment: Payment): Either<Error, Payment> {

    }

}
