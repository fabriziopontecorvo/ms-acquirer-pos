package com.prismamp.todopago.model

import com.prismamp.todopago.payment.adapter.repository.model.AccountResponse
import com.prismamp.todopago.payment.domain.model.Account

fun anAccountResponse() =
    AccountResponse(
        id = "1",
        accountStatus = "status",
        denomination1 = "name",
        denomination2 = "surname",
        email = "email@same.com",
        identificationNumber = "123",
        identificationTypeId = "type",
        gender = "male"
    )

fun anAccount() =
    Account(
        id = 1,
        status = "status",
        firstName = "name",
        lastName = "surname",
        email = "email@same.com",
        identification = "123",
        identificationType = "type",
        gender = "male"
    )
