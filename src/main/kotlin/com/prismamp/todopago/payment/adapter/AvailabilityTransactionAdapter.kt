package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Option
import arrow.core.handleErrorWith
import arrow.core.rightIfNull
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.dao.QrDao
import com.prismamp.todopago.payment.application.port.out.CheckAvailabilityOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.QrUSed
import org.springframework.stereotype.Component

@Component
class AvailabilityTransactionAdapter(
    private val qrCache: QrCache,
    private val qrDao: QrDao
) : CheckAvailabilityOutputPort {

    override suspend fun Payment.checkAvailability(): Either<ApplicationError, Payment> =
        checkCache()
            .handleErrorWith { checkDatabase() }

    private suspend fun Payment.checkCache() =
        qrCache.fetchPayment(this)
            .ifIsAvailableMapToDomain(this, QrUSed(qrId))

    private suspend fun Payment.checkDatabase() =
        qrDao.findQrOperationBy(buildFilters())
            .ifIsAvailableMapToDomain(this, QrUSed(qrId))

    private fun <T, K> Option<K>.ifIsAvailableMapToDomain(domain: T, applicationError: ApplicationError) =
        rightIfNull { applicationError }
            .map { domain }

    private fun Payment.buildFilters() = mapOf(
        "qr_id" to qrId,
        "amount" to amount,
        "pos_terminal_number" to establishmentInformation.terminalNumber,
        "transaction_timestamp" to transactionDatetime
    )


}
