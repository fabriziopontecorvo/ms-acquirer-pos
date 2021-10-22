package com.prismamp.todopago.util

import arrow.core.Either
import arrow.core.right
import com.prismamp.todopago.commons.rest.exception.*
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.enum.PosType
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
            "An operation with qr_id has already been performed = $qrId"
        )
        is InvalidAccount -> UnprocessableEntityException(
            "INVALID_ACCOUNT",
            "The account $accountId is not valid for this operation"
        )
        is NotMatchableInstallments -> UnprocessableEntityException(
            "NOT_MATCHABLE_INSTALLMENTS",
            "The amount of fees required do not match those enabled for the payment method"
        )
        is InvalidBenefit -> UnprocessableEntityException(
            "INVALID_BENEFIT",
            "No benefits found for the recommendation_code $benefitId"
        )
        is SecurityCodeRequired -> ConflictException(
            "SECURITY_CODE_REQUIRED",
            "CVV code is required for this operation"
        )
        is InvalidPaymentMethod -> UnprocessableEntityException(
            "INVALID_PAYMENT_METHOD",
            "The payment method $paymentMethod is invalid for this operation"
        )
        is CheckBenefitError -> UnprocessableEntityException(
            "CHECK_BENEFIT_ERROR",
            "An error occurred when checking the benefit $benefitNumber"
        )
        is IdProviderFailure -> UnprocessableEntityException(
            "ID_PROVIDER_FAILURE",
            "Could not get an id for persistence"
        )
        is LimitValidationError -> UnprocessableEntityException(
            "LIMIT_EXCEEDED",
            "The payment cannot be made because the alert or rejection limit has been exceeded:: $limitReport"
        )
        is BenefitFieldsBadRequest -> BadRequestException(
            "BENEFIT_FIELDS_EXCEPTION",
            "benefit_id and shopping_session_id are complementarity"
        )
    }

