package com.prismamp.todopago.transfer.domain.model

import java.time.ZonedDateTime

data class Transfer(
    val response: TransferResponse,
    val debin: Debin,
    val evaluate: TransferEvaluate
){
    data class TransferResponse(
        val description: String,
        val code: String
    )

    data class Debin(
        val id: String,
        val status: DebinStatus,
        val date: ZonedDateTime,
        val expirationDate: ZonedDateTime
    )

    data class DebinStatus(
        val code: String,
        val description: String
    )

    data class TransferEvaluate(
        val score: String,
        val rules: String
    )

}
