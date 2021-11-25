package com.prismamp.todopago.transfer.adapter.repository.model

import com.prismamp.todopago.transfer.domain.model.Transfer
import java.time.ZonedDateTime

data class CoelsaTransferResponse(
    val respuesta: Respuesta,
    val debin: Debin,
    val evaluacion: Evaluacion
) {
    data class Respuesta(
        val descripcion: String,
        val codigo: String
    )

    data class Debin(
        val id: String,
        val estado: Estado,
        val addDt: ZonedDateTime,
        val fechaExpiracion: ZonedDateTime
    )

    data class Estado(
        val codigo: String,
        val descripcion: String
    )

    data class Evaluacion(
        val puntaje: String,
        val reglas: String
    )

    fun toDomain() = Transfer(
        response = Transfer.TransferResponse(
            description = respuesta.descripcion,
            code = respuesta.codigo
        ),
        debin = Transfer.Debin(
            id = debin.id,
            status = Transfer.DebinStatus(
                code = debin.estado.codigo,
                description = debin.estado.descripcion
            ),
            date = debin.addDt,
            expirationDate = debin.fechaExpiracion
        ),
        evaluate = Transfer.TransferEvaluate(
            score = evaluacion.puntaje,
            rules = evaluacion.reglas
        )
    )
}