package com.prismamp.todopago.transfer.domain.model

import com.prismamp.todopago.enum.Concept
import com.prismamp.todopago.enum.Currency
import com.prismamp.todopago.enum.Currency.ARS

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
        val currency: Currency = ARS,
        val amount: String,
        val expirationTime: String,
        val description: String,
        val qr: String,
        val qrTransactionId: String,
        val walletId: String
    )

}
