package com.prismamp.todopago.util

import arrow.core.Either
import arrow.core.right
import com.prismamp.todopago.commons.rest.exception.*
import com.prismamp.todopago.configuration.Constants
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.payment.adapter.command.model.exception.LockedQrException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpStatusCodeException


fun <T> Either<Either<ApplicationError, T>, T>.leftFlatten(): Either<ApplicationError, T> =
    when (this) {
        is Either.Right -> value.right()
        is Either.Left -> value
    }

fun <T> Either<ApplicationError, T>.evaluate() =
    fold(
        ifLeft = { applicationError -> throw applicationError.exceptionManager() },
        ifRight = { value -> value }
    )

fun <T> ResponseEntity<T>.handleSuccess() = body!!

fun Throwable.handleFailure(receiver: String, customHandler: ((HttpStatusCodeException) -> ApplicationError)? = null) =
    when (this) {
        is HttpStatusCodeException -> customHandler?.let { it(this) } ?: handleHttpFailure(this, receiver)
        else -> ServiceCommunication(APP_NAME, receiver)
    }

private fun handleHttpFailure(exception: HttpStatusCodeException, receiver: String): ApplicationError =
    when (exception.statusCode) {
        HttpStatus.BAD_REQUEST -> BadRequest(exception.responseBodyAsString)
        HttpStatus.NOT_FOUND -> NotFound(exception.responseBodyAsString)
        HttpStatus.UNPROCESSABLE_ENTITY -> UnprocessableEntity(exception.responseBodyAsString)
        else -> ServiceCommunication(APP_NAME, receiver)
    }

private fun ApplicationError.exceptionManager(): HttpException =
    when (this) {
        is BadRequest -> BadRequestException(body)
        is NotFound -> NotFoundException(body)
        is UnprocessableEntity -> UnprocessableEntityException(body)
        is ServiceCommunication -> ServiceCommunicationException(transmitter, receiver)
        is LockedQr -> LockedQrException(uniqueLockKey)
        is QrUSed -> UnprocessableEntityException(
            "QR_USED",
            "Ya se realizó una operación con qrId = $qrId"
        )
        is InvalidAccount -> UnprocessableEntityException(
            "INVALID_ACCOUNT",
            "La cuenta con id: $accountId no es valida para esta operacion"
        )
        is NotMatchableInstallments -> UnprocessableEntityException(
            "NOT_MATCHABLE_INSTALLMENTS",
            "La cantidad de cuotas requeridas no coinciden con las habilitadas para el medio de pago"
        )
        is InvalidBenefit -> UnprocessableEntityException(
            "INVALID_BENEFIT",
            "No se encontraron beneficios para el recomendation_code $benefitId"
        )
        is SecurityCodeRequired -> ConflictException(
            "SECURITY_CODE_REQUIRED",
            "El código CVV es requerido para esta operacion"
        )
        is InvalidPaymentMethod -> UnprocessableEntityException(
            "INVALID_PAYMENT_METHOD",
            "El medio de pago $paymentMethod es invalido para esta operacion"
        )
        is CheckBenefitError -> UnprocessableEntityException(
            "CHECK_BENEFIT_ERROR",
            "Ocurrio un error al checkear el beneficio $benefitNumber"
        )
        IdProviderFailure -> UnprocessableEntityException(
            "ID_PROVIDER_FAILURE",
            "No se pudo obtener un id para la persistencia"
        )
        is LimitValidationError -> UnprocessableEntityException(
            "LIMIT_EXCEEDED",
            "El pago no puede realizarse porque se ha superado el limite de alerta/rechazo: $limitReport"
        )

    }

