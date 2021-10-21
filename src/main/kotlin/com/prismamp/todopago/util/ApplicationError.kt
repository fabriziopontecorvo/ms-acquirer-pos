package com.prismamp.todopago.util

sealed interface ApplicationError

data class BadRequest(val body: String) : ApplicationError
data class UnprocessableEntity(val body: String) : ApplicationError
data class NotFound(val body: String) : ApplicationError
data class ServiceCommunication(val transmitter: String, val receiver: String) : ApplicationError
data class LockedQr(val uniqueLockKey:String): ApplicationError
data class QrUSed(val qrId: String) : ApplicationError
data class InvalidAccount(val accountId: String): ApplicationError
data class InvalidBenefit(val benefitId: String): ApplicationError
data class InvalidPaymentMethod(val paymentMethod: String): ApplicationError
data class CheckBenefitError(val benefitNumber: String): ApplicationError
data class LimitValidationError(val limitReport: String): ApplicationError
object IdProviderFailure: ApplicationError
object SecurityCodeRequired: ApplicationError
object NotMatchableInstallments : ApplicationError
