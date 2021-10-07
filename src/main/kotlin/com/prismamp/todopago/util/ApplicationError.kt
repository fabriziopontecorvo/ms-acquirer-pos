package com.prismamp.todopago.util

sealed interface ApplicationError

class BadRequest(val body: String) : ApplicationError
class UnprocessableEntity(val body: String) : ApplicationError
class NotFound(val body: String) : ApplicationError
class ServiceCommunication(val transmitter: String, val receiver: String) : ApplicationError
class LockedQr(val uniqueLockKey:String): ApplicationError
class QrUSed(val qrId: String) : ApplicationError
class InvalidAccount(val accountId: String): ApplicationError
object NotMatchableInstallments : ApplicationError
class InvalidBenefit(val benefitId: String): ApplicationError
object SecurityCodeRequired: ApplicationError
class InvalidPaymentMethod(val paymentMethod: String): ApplicationError
class CheckBenefitError(val benefitNumber: String): ApplicationError
object IdProviderFailure: ApplicationError
