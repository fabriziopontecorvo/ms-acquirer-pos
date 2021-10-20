package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.payment.adapter.repository.rest.AccountClient
import com.prismamp.todopago.payment.application.port.out.AccountOutputPort
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class AccountAdapter(
    private val accountClient: AccountClient,
) : AccountOutputPort {

    override suspend fun Operation.getAccount(): Either<ApplicationError, Account> =
        accountClient.getAccountBy(accountId.toString())
}
