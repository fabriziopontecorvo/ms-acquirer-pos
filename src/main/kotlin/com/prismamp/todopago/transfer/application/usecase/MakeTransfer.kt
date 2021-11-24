package com.prismamp.todopago.transfer.application.usecase

import arrow.core.Either
import com.prismamp.todopago.transfer.application.port.`in`.MakeTransferInputPort
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError

class MakeTransfer() : MakeTransferInputPort  {

    override suspend fun execute(operation: Operation): Either<ApplicationError, Any> =
        TODO("Not yet implemented")


}
