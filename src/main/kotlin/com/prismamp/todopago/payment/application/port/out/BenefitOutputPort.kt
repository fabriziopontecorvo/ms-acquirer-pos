package com.prismamp.todopago.payment.application.port.out

import arrow.core.Either
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.Payment
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
interface BenefitOutputPort {
    suspend fun Payment.checkBenefit(): Either<ApplicationError, Benefit?>
}
