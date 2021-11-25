package com.prismamp.todopago.transfer.application.port.`in`

import arrow.core.Either
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.transfer.domain.model.Transfer
import com.prismamp.todopago.util.ApplicationError

interface MakeTransferInputPort {
   suspend fun execute(operation: Operation): Either<ApplicationError, Transfer>
}
