package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.handleErrorWith
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.dao.QrDao
import com.prismamp.todopago.payment.application.port.out.CheckAvailabilityOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.Error
import org.springframework.stereotype.Component

@Component
class AvailabilityTransactionAdapter(
    private val qrCache: QrCache,
    private val qrDao: QrDao
) : CheckAvailabilityOutputPort {

    override suspend fun Payment.checkAvailability(): Either<Error, Payment> =
        qrCache
            .checkAvailability(this)
            .handleErrorWith { qrDao.checkAvailability(this) }

}
