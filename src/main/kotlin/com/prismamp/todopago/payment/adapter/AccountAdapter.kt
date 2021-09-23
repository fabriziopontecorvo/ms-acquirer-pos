package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.payment.adapter.repository.rest.AccountClient
import com.prismamp.todopago.payment.application.port.out.AccountOutputPort
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class AccountAdapter(
    private val accountClient: AccountClient,
) : AccountOutputPort {

    override suspend fun Payment.getAccount(): Either<ApplicationError, Account> =
        accountClient.getAccountBy(accountId.toString())
}
