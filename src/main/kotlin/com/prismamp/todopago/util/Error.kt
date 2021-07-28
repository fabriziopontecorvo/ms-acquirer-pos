package com.prismamp.todopago.util

sealed interface Error

class BadRequest(val body: String) : Error
class UnprocessableEntity(val body: String) : Error
class NotFound(val body: String) : Error
class ServiceCommunication(
    val transmitter: String,
    val receiver: String
) : Error
