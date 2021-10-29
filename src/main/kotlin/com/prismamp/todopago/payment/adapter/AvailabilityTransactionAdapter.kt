package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Option
import arrow.core.toOption
import com.prismamp.todopago.payment.adapter.repository.cache.QrCache
import com.prismamp.todopago.payment.adapter.repository.dao.QrDao
import com.prismamp.todopago.payment.application.port.out.CheckAvailabilityOutputPort
import com.prismamp.todopago.payment.domain.model.Operation
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

    override suspend fun Operation.checkAvailability(): Either<ApplicationError, Operation> =
        qrCache
            .fetchOperation(this)
            .validate(this, QrUSed(qrId))
            .log { info("checkAvailability: resultado {}", it) }

    private suspend fun <K> Option<K>.validate(
        domain: Operation,
        applicationError: ApplicationError
    ) =
        takeIf { isDefined() }
            ?.let {
                Either.Left(applicationError)
            } ?: domain.checkDatabase()

    private suspend fun Operation.checkDatabase() =
        qrDao
            .findQrOperationBy(buildFilters())
            .ifIsAvailableMapToDomain(this, QrUSed(qrId))

    private fun <T, K, S> Either<K, S>.ifIsAvailableMapToDomain(domain: T, applicationError: ApplicationError) =
        takeIf { isLeft() }
            ?.let {
               domain.toOption().toEither { applicationError }
            } ?: Either.Left(applicationError)

    private fun Operation.buildFilters() = mapOf(
        "qr_id" to qrId,
        "amount" to amount,
        "pos_terminal_number" to establishmentInformation.terminalNumber,
        "transaction_timestamp" to transactionDatetime
    )


}
