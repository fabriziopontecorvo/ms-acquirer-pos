package com.prismamp.todopago.transfer.adapter.repository.model

import com.prismamp.todopago.transfer.domain.model.Operation

data class CoelsaTransferRequest(
    val vendedor: Seller,
    val comprador: Buyer,
    val detalle: QrDetail
) {
    data class Seller(
        val cuit: String,
        val cbu: String,
        val banco: String,
        val sucursal: String,
        val terminal: String
    )

    data class Buyer(
        val cuenta: Account,
        val cuit: String
    )

    data class Account(
        val cbu: String
    )

    data class QrDetail(
        val concepto: String,
        val moneda: String,
        val importe: String,
        val tiempoExpiracion: String,
        val descripcion: String,
        val qr: String,
        val qrIdTrx: String,
        val idBilletera: String
    )

    companion object {
        fun from(operation: Operation) = with(operation) {
            CoelsaTransferRequest(
                vendedor = Seller(
                    cuit = seller.cuit,
                    cbu = seller.cbu,
                    banco = seller.bank,
                    sucursal = seller.branch,
                    terminal = seller.terminal
                ),
                comprador = Buyer(
                    cuenta = Account(cbu = buyer.cbu),
                    cuit = buyer.cuit
                ),
                detalle = QrDetail(
                    concepto = detail.concept.value,
                    moneda = detail.currency.value,
                    importe = detail.amount,
                    tiempoExpiracion = detail.expirationTime,
                    descripcion = detail.description,
                    qr = detail.qr,
                    qrIdTrx = detail.qrTransactionId,
                    idBilletera = detail.walletId
                )
            )
        }
    }

}