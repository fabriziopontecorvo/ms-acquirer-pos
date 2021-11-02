package com.prismamp.todopago.payment.domain.model

import com.prismamp.todopago.enum.PaymentStatusRequest
import com.prismamp.todopago.enum.PaymentStatusRequest.INVALID
import java.util.*

data class GatewayResponse(
    val statusRequest: PaymentStatusRequest = INVALID,
    val transactionDatetime: Date = Date(),
    val id: Long = -1,
    val status: String = "",
    val statusDetails: DecidirResponseStatusDetails = DecidirResponseStatusDetails()
) {
    data class DecidirResponseStatusDetails(
        val cardAuthorizationCode: String = "",
        val cardReferenceNumber: String = "",
        val response: DecidirResponseStatusResponse = DecidirResponseStatusResponse()
    ){
        companion object{
            fun isInvalid(statusDetails: DecidirResponseStatusDetails) =
                statusDetails == DecidirResponseStatusDetails()
        }
    }

    data class DecidirResponseStatusResponse(
        val type: String = "",
        val reason: DecidirResponseReason = DecidirResponseReason()
    )

    data class DecidirResponseReason(
        val id: Int = 0,
        val description: String = "",
        val additionalDescription: String = ""
    ){
        companion object{
            fun isInvalid(reason: DecidirResponseReason) =
                reason == DecidirResponseReason()
        }
    }
}
