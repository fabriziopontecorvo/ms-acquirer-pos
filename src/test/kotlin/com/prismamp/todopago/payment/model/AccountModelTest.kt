package com.prismamp.todopago.payment.model

import com.prismamp.todopago.payment.adapter.repository.model.AccountResponse
import com.prismamp.todopago.payment.domain.model.Account

fun anAccountResponse() =
    AccountResponse(
        id = "1",
        accountStatus = "CTA_HABILITADA",
        denomination1 = "name",
        denomination2 = "surname",
        email = "email@same.com",
        identificationNumber = "123",
        identificationTypeId = "39",
        gender = "male"
    )

fun anAccount() =
    Account(
        id = 1,
        status = "CTA_HABILITADA",
        firstName = "name",
        lastName = "surname",
        email = "email@same.com",
        identification = "123",
        identificationType = "39",
        gender = "male"
    )
