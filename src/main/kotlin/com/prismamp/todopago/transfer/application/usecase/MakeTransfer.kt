package com.prismamp.todopago.transfer.application.usecase

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import com.prismamp.todopago.configuration.annotation.UseCase
import com.prismamp.todopago.transfer.application.port.`in`.MakeTransferInputPort
import com.prismamp.todopago.transfer.application.port.out.*
import com.prismamp.todopago.transfer.domain.model.Operation
import com.prismamp.todopago.transfer.domain.model.Transfer
import com.prismamp.todopago.util.ApplicationError

@UseCase
class MakeTransfer(
    transferLockOutputPort: TransferLockOutputPort,
    virtualAccountOutputPort: VirtualAccountOutputPort,
    transferOutputPort: TransferOutputPort,
    transferPersistenceOutputPort: TransferPersistenceOutputPort,
    transferReleaseOutputPort: TransferReleaseOutputPort,
) : MakeTransferInputPort,
    TransferLockOutputPort by transferLockOutputPort,
    VirtualAccountOutputPort by virtualAccountOutputPort,
    TransferOutputPort by transferOutputPort,
    TransferPersistenceOutputPort by transferPersistenceOutputPort,
    TransferReleaseOutputPort by transferReleaseOutputPort {

    override suspend fun execute(operation: Operation) =
        operation
            .lock()
            .checkVirtualAccount()
            .executeTransfer()
            .persist()
            .also { operation.release() }


    private suspend fun Either<ApplicationError, Operation>.checkVirtualAccount(): Either<ApplicationError, Operation> =
        flatMap {
            either {
                it.checkVirtualAccount().bind()
                it
            }
        }

    private suspend fun Either<ApplicationError, Operation>.executeTransfer(): Either<ApplicationError, Transfer> =
        flatMap { it.makeTransfer() }

    private suspend fun Either<ApplicationError, Transfer>.persist() =
        flatMap { it.persist() }
}
