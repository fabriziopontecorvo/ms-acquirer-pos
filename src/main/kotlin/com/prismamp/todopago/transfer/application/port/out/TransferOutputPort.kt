package com.prismamp.todopago.transfer.application.port.out

import arrow.core.Either
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.transfer.domain.model.Transfer
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface TransferOutputPort {
    suspend fun Operation.makeTransfer(): Either<ApplicationError, Transfer>
}
