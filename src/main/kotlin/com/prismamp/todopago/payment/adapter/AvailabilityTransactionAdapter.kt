package com.prismamp.todopago.payment.adapter

import arrow.core.*
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.dao.QrDao
import com.prismamp.todopago.payment.application.port.out.CheckAvailabilityOutputPort
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.QrUSed
import com.prismamp.todopago.util.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AvailabilityTransactionAdapter(
    private val qrCache: QrCache,
    private val qrDao: QrDao
) : CheckAvailabilityOutputPort {

    companion object: CompanionLogger()

    override suspend fun Payment.checkAvailability(): Either<ApplicationError, Payment> =
        qrCache
            .fetchPayment(this)
            .validate(this, QrUSed(qrId))
            .ifIsAvailableMapToDomain(this, QrUSed(qrId))
            .log { info("checkAvailability: resultado {}", it) }

    private suspend fun <K> Option<K>.validate(
        domain: Payment,
        applicationError: ApplicationError
    ) =
        takeIf { isDefined() }
            ?.let {
                Either.Left(applicationError)
            } ?: domain.checkDatabase()

    private suspend fun Payment.checkDatabase() =
        qrDao
            .findQrOperationBy(buildFilters())

    private fun <T, K, S> Either<K, S>.ifIsAvailableMapToDomain(domain: T, applicationError: ApplicationError) =
        takeIf { isLeft() }
            ?.let {
               domain.toOption().toEither { applicationError }
            } ?: Either.Left(applicationError)

    private fun Payment.buildFilters() = mapOf(
        "qr_id" to qrId,
        "amount" to amount,
        "pos_terminal_number" to establishmentInformation.terminalNumber,
        "transaction_timestamp" to transactionDatetime
    )


}
