package com.prismamp.todopago.util

import arrow.core.Either
import com.prismamp.todopago.commons.rest.exception.*
import com.prismamp.todopago.payment.adapter.command.model.exception.LockedQrException

fun <T> Either<Error, T>.evaluate() =
    when (this) {
        is Either.Left -> value.exceptionManager()
        is Either.Right -> value
    }

private fun Error.exceptionManager(): Nothing =
    when (this) {
        is BadRequest -> throw BadRequestException(this.body)
        is NotFound -> throw NotFoundException(this.body)
        is UnprocessableEntity -> throw UnprocessableEntityException(this.body)
        is ServiceCommunication -> throw ServiceCommunicationException(this.transmitter, this.receiver)
        is LockedQr -> throw LockedQrException(this.uniqueLockKey)
    }
