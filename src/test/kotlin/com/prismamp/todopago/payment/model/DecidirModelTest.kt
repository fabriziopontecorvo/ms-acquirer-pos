import com.prismamp.todopago.Message
import com.prismamp.todopago.enum.PaymentStatusRequest
import com.prismamp.todopago.payment.model.aValidatableOperation
import com.prismamp.todopago.payment.adapter.repository.model.DecidirResponse
import com.prismamp.todopago.payment.domain.model.GatewayRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import java.time.LocalDateTime

fun aDecidirResponseReason(id: Int = 1) =
    GatewayResponse.DecidirResponseReason(
        id = id,
        description = "Error al realizar la compra",
        additionalDescription = ""
    )

fun aMessage() =
    Message(
        id = "1",
        code = "071",
        text = "Error al realizar la compra"
    )

fun aGatewayRequest() =
    GatewayRequest(
        qrId = "qrId",
        posType = "com.adq",
        establishmentId = "establishment",
        transactionDatetime =  LocalDateTime.of(2021,11,8,23,59,59),
        paymentMethodId = 1,
        cardData = GatewayRequest.DecidirRequestCard(
            cardNumber = "cardNumber",
            cardExpirationYear = "2021",
            cardExpirationMonth = "12",
            securityCode = "123",
            bankData = GatewayRequest.DecidirRequestBank(
                1, "description"
            )
        ),
        amount = 100.00,
        currency = "ARS",
        installments = 1,
        terminalData = GatewayRequest.DecidirRequestTerminalData(
            traceNumber = "1234",
            ticketNumber = "1234",
            terminalNumber = "1111"
        ),
        benefitsData = GatewayRequest.BenefitsData(
            GatewayRequest.BenefitsCard(
                code = "code",
                description = "description"
            ),
            originalAmount = 200.00,
            discountedAmount = 100.00
        )
    )

fun aGatewayRequestFromValidatable() =
    GatewayRequest.from(aValidatableOperation())

fun aDecidirResponse() =
    DecidirResponse(
        transactionDatetime = LocalDateTime.of(2021,11,8,23,59,59),
        id = 1,
        status = "status",
        statusDetails = DecidirResponse.DecidirResponseStatusDetails(
            cardAuthorizationCode = "card",
            cardReferenceNumber = "cardNumber",
            response = DecidirResponse.DecidirResponseStatusResponse(
                type = "type",
                reason = DecidirResponse.DecidirResponseReason(
                    id = 1,
                    description = "",
                    additionalDescription = ""
                )
            )
        )
    )

fun aGatewayResponse() =
    GatewayResponse(
        PaymentStatusRequest.SUCCESS,
        transactionDatetime = LocalDateTime.of(2021,11,8,23,59,59),
        id = 1,
        status = "status",
        statusDetails = GatewayResponse.DecidirResponseStatusDetails(
            cardAuthorizationCode = "card",
            cardReferenceNumber = "cardNumber",
            response = GatewayResponse.DecidirResponseStatusResponse(
                type = "type",
                reason = GatewayResponse.DecidirResponseReason(
                    id = 1,
                    description = "",
                    additionalDescription = ""
                )
            )
        )

    )
