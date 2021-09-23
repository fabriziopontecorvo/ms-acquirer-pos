package com.prismamp.todopago.util

import arrow.core.Either
import com.prismamp.todopago.commons.rest.exception.*
import com.prismamp.todopago.payment.adapter.command.model.exception.LockedQrException
import org.springframework.http.ResponseEntity

fun <T> handleSuccess(responseEntity: ResponseEntity<T>) =
    responseEntity
        .body!!

fun <T> Either<ApplicationError, T>.evaluate() =
    when (this) {
        is Either.Left -> value.exceptionManager()
        is Either.Right -> value
    }

private fun ApplicationError.exceptionManager(): Nothing =
    when (this) {
        is BadRequest -> throw BadRequestException(body)
        is NotFound -> throw NotFoundException(body)
        is UnprocessableEntity -> throw UnprocessableEntityException(body)
        is ServiceCommunication -> throw ServiceCommunicationException(transmitter, receiver)
        is LockedQr -> throw LockedQrException(uniqueLockKey)
        is QrUSed -> throw UnprocessableEntityException("QR_USED", "Ya se realizó una operación con qrId = $qrId")
        is InvalidAccount -> throw  UnprocessableEntityException(
            "INVALID_ACCOUNT",
            "La cuenta con id: $accountId no es valida para esta operacion"
        )
        is NotMatchableInstallments -> throw UnprocessableEntityException(
            "NOT_MATCHABLE_INSTALLMENTS",
            "La cantidad de cuotas requeridas no coinciden con las habilitadas para el medio de pago"
        )
        is InvalidBenefit -> throw  UnprocessableEntityException(
            "INVALID_BENEFIT",
            "No se encontraron beneficios para el recomendation_code $benefitId"
        )
        is SecurityCodeRequired -> throw ConflictException(
            "SECURITY_CODE_REQUIRED",
            "El código CVV es requerido para esta operacion"
        )
        is InvalidPaymentMethod -> throw UnprocessableEntityException(
            "INVALID_PAYMENT_METHOD",
            "El medio de pago $paymentMethod es invalido para esta operacion"
        )
        is CheckBenefitError ->  throw UnprocessableEntityException(
            "CHECK_BENEFIT_ERROR",
            "Ocurrio un error al checkear el beneficio $benefitNumber"
        )
    }
