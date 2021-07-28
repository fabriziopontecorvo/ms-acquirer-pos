package com.prismamp.todopago.payment.adapter.command.controller

import com.prismamp.todopago.commons.rest.annotation.HttpController
import com.prismamp.todopago.commons.security.OAuth2Authorization
import com.prismamp.todopago.configuration.annotation.OAuth2TokenBodyValidationAccountId
import com.prismamp.todopago.payment.adapter.command.model.PaymentRequest
import com.prismamp.todopago.payment.adapter.command.model.PaymentResponse
import com.prismamp.todopago.payment.application.port.`in`.MakePaymentInputPort
import com.prismamp.todopago.util.evaluate
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark

import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api
@RestController
@HttpController
class PaymentController(
    val makePaymentInputPort: MakePaymentInputPort
) {

    companion object : CompanionLogger()

    @OAuth2Authorization
    @OAuth2TokenBodyValidationAccountId
    @PostMapping("/public/v1/payments")
    fun executePayment(@Valid @RequestBody request: PaymentRequest) =
        log.benchmark("POST execute payment") {
            log { info("executePayment: request: {}", request) }
            makePaymentInputPort
                .execute(request.toDomain())
                .map { PaymentResponse.from(it) }
                .evaluate()
        }

}