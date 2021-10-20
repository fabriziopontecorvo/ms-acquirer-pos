package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import com.prismamp.todopago.enum.Channel.QRADQ
import com.prismamp.todopago.payment.adapter.repository.model.CheckBenefitRequest
import com.prismamp.todopago.payment.adapter.repository.rest.BenefitCheckClient
import com.prismamp.todopago.payment.application.port.out.BenefitOutputPort
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import org.springframework.stereotype.Component

@Component
class BenefitAdapter(
    private val benefitCheckClient: BenefitCheckClient
) : BenefitOutputPort {

    override suspend fun Operation.checkBenefit(): Either<ApplicationError, Benefit?> =
        benefitCheckClient.check(benefitNumber, buildRequest())

    private fun Operation.buildRequest() = CheckBenefitRequest(
        installments = installments,
        amount = amount,
        originalAmount = originalAmount,
        discountedAmount = discountedAmount,
        benefitCardCode = benefitCardCode,
        benefitCardDescription = benefitCardDescription,
        shoppingSessionId = shoppingSessionId,
        operationType = QRADQ
    )
}
