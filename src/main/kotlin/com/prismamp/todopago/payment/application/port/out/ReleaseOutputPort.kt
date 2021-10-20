package com.prismamp.todopago.payment.application.port.out

import com.prismamp.todopago.payment.domain.model.Operation
import org.springframework.stereotype.Component

@Component
interface ReleaseOutputPort {
    suspend fun Operation.release()
}
