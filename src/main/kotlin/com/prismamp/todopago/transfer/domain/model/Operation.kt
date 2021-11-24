package com.prismamp.todopago.transfer.domain.model

import com.prismamp.todopago.enum.Concept

data class Operation(
    val seller: Seller,
    val buyer: Buyer,
    val detail: QrDetail
) {
    data class Seller(
        val cuit: String,
        val cbu: String,
        val bank: String,
        val branch: String,
        val terminal: String
    )

    data class Buyer(
        val cbu: String,
        val cuit: String
    )

    data class QrDetail(
        val concept: Concept,
        val userId: String,
        val ticketId: String,
        val currency: String,
        val expirationTime: String,
        val description: String,
        val qr: String,
        val qrHash: String,
        val qrTransactionId: String,
        val walletId: String
    )

}
