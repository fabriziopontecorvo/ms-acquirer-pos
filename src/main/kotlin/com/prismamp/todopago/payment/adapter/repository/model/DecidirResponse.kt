package com.prismamp.todopago.payment.adapter.repository.model

import com.prismamp.todopago.enum.PaymentStatusRequest
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import java.util.*

data class DecidirResponse(
    val transactionDatetime: Date = Date(),
    val id: Long = -1,
    val status: String = "",
    val statusDetails: DecidirResponseStatusDetails = DecidirResponseStatusDetails()
) {
    data class DecidirResponseStatusDetails(
        val cardAuthorizationCode: String = "",
        val cardReferenceNumber: String = "",
        val response: DecidirResponseStatusResponse = DecidirResponseStatusResponse()
    )

    data class DecidirResponseStatusResponse(
        val type: String = "",
        val reason: DecidirResponseReason = DecidirResponseReason()
    )

    data class DecidirResponseReason(
        val id: Int = 0,
        val description: String = "",
        val additionalDescription: String = ""
    )

    fun toDomain(statusRequest: PaymentStatusRequest) =
        GatewayResponse(
            statusRequest = statusRequest,
            transactionDatetime = transactionDatetime,
            id = id,
            status = status,
            statusDetails = with(statusDetails) {
                GatewayResponse.DecidirResponseStatusDetails(
                    cardAuthorizationCode = cardAuthorizationCode,
                    cardReferenceNumber = cardReferenceNumber,
                    response = GatewayResponse.DecidirResponseStatusResponse(
                        type = response.type,
                        reason = GatewayResponse.DecidirResponseReason(
                            id = response.reason.id,
                            description = response.reason.description,
                            additionalDescription = response.reason.additionalDescription
                        )
                    )
                )
            }
        )
}
