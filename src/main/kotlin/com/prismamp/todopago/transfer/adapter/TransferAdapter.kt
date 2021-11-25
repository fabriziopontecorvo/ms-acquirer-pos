package com.prismamp.todopago.transfer.adapter

import arrow.core.Either
import com.prismamp.todopago.transfer.application.port.out.TransferOutputPort
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.transfer.domain.model.Transfer
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class TransferAdapter : TransferOutputPort {
    override suspend fun Operation.makeTransfer(): Either<ApplicationError, Transfer> {

    }
}