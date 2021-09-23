package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.payment.domain.model.Account

data class AccountResponse(
    val id: String,
    val accountStatus: String,
    val denomination1: String,
    val denomination2: String,
    val email: String,
    val identificationNumber: String,
    val identificationTypeId: String,
    val gender: String
) {

    fun toDomain() =
        Account(
            id = id.toLongOrNull() ?: 0,
            status = accountStatus,
            firstName = denomination1,
            lastName = denomination2,
            email = email,
            identification = identificationNumber,
            identificationType = identificationTypeId,
            gender = gender
        )
}
