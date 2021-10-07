package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.payment.domain.model.Account as DomainAccount

data class Account(
    val id: Long = -1,
    val status: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val identification: String = "",
    val identificationType: String = "",
    val gender: String = ""
) {
    companion object {
        fun from(account: DomainAccount) =
            with(account) {
                Account(
                    id = id,
                    status = status,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    identification = identification,
                    identificationType = identificationType,
                    gender = gender
                )
            }
    }
}
