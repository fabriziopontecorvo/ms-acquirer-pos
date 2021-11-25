package com.prismamp.todopago.transfer.application.port.out

import com.prismamp.todopago.transfer.domain.model.Operation
import org.springframework.stereotype.Component

@Component
interface TransferReleaseOutputPort {
    suspend fun Operation.release()
}
