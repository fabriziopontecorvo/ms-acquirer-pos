package com.prismamp.todopago.transfer.adapter

import arrow.core.Either
import com.prismamp.todopago.transfer.adapter.repository.model.CoelsaTransferRequest
import com.prismamp.todopago.transfer.adapter.repository.rest.CoelsaClient
import com.prismamp.todopago.transfer.application.port.out.TransferOutputPort
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.transfer.domain.model.Transfer
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class TransferAdapter(
    val coelsaClient: CoelsaClient
) : TransferOutputPort {

    override suspend fun Operation.makeTransfer(): Either<ApplicationError, Transfer> =
        coelsaClient.makeTransfer(CoelsaTransferRequest.from(operation = this))

}
