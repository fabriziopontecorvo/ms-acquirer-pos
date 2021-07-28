package com.prismamp.todopago.configuration.handler.controller

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.prismamp.todopago.commons.rest.annotation.HttpController
import com.prismamp.todopago.commons.rest.response.ErrorItemResponse
import com.prismamp.todopago.commons.rest.response.ErrorResponse
import com.prismamp.todopago.commons.tenant.UnknownTenantException
import com.prismamp.todopago.util.logs.CompanionLogger
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = [HttpController::class])
class ControllerAdvice {

    companion object : CompanionLogger() {
        private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingServletRequestParamErrorHandler(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> =
        with(e) {
            log.warn("handling http exception: status = {}, body = {}", 400, message)
            ErrorResponse(
                ErrorItemResponse(
                    "400",
                    "BAD_REQUEST",
                    message
                )
            )
        }.let {
            status(BAD_REQUEST).body(it)
        }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        also {
            log.warn("handling HttpMessageNotReadableException: {}", e.localizedMessage, e)
        }.let {
            ErrorResponse(
                ErrorItemResponse(
                    "400",
                    "BAD_REQUEST",
                    getTitleFrom(e)
                )
            )
        }.let {
            status(BAD_REQUEST).body(it)
        }

    @ExceptionHandler(UnknownTenantException::class)
    fun unknownTenantErrorHandler(e: UnknownTenantException): ResponseEntity<ErrorResponse> =
        also {
            log.warn("handling unknown tenant exception: {}", e.localizedMessage, e)
        }.let {
            ErrorResponse(
                ErrorItemResponse(
                    "422",
                    "UNPROCESSABLE_ENTITY",
                    "Unknown tenant: ${e.tenantId}"
                )
            )
        }.let {
            status(HttpStatus.UNPROCESSABLE_ENTITY).body(it)
        }


    @ExceptionHandler(InvalidFormatException::class)
    fun dateTimeRequestErrorHandler(e: InvalidFormatException): ResponseEntity<ErrorResponse> =
        also {
            log.warn("handling invalid format exception: {}", e.localizedMessage, e)
        }.let {
            ErrorResponse(
                ErrorItemResponse(
                    "400",
                    "BAD_REQUEST",
                    "Invalid field: ${camelToSnakeCase(e.path[0].fieldName ?: "unknown")}"
                )
            )
        }.let {
            status(BAD_REQUEST).body(it)
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidRequestErrorHandler(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        also {
            log.warn("handling invalid field exception: {}", e.localizedMessage, e)
        }.let {
            ErrorResponse(
                ErrorItemResponse(
                    "400",
                    "BAD_REQUEST",
                    e.bindingResult.fieldErrors
                        .fold("") { acc, err ->
                            acc + err.defaultMessage.toString().plus(". ")
                        }
                )
            )
        }.let {
            status(BAD_REQUEST).body(it)
        }

    private fun camelToSnakeCase(source: CharSequence): String {
        return camelRegex.replace(source) {
            "_${it.value}"
        }.lowercase()
    }

    private fun getTitleFrom(e: HttpMessageNotReadableException) =
        when (e.cause) {
            is MissingKotlinParameterException ->
                "Field '${camelToSnakeCase((e.cause as MissingKotlinParameterException).parameter.name ?: "unknown")}' is mandatory."
            else -> "The request can not be parsed."
                .plus(" Field ")
                .plus(e.localizedMessage.substringAfter("[\"").substringBefore("\"])"))
        }
}
