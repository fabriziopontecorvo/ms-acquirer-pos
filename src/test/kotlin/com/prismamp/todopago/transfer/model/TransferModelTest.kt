package com.prismamp.todopago.transfer.model

import com.prismamp.todopago.transfer.domain.model.Transfer
import java.time.LocalDateTime.MAX
import java.time.LocalDateTime.MIN
import java.time.ZoneId
import java.time.ZonedDateTime

fun aTransfer() =
    Transfer(
        response = Transfer.TransferResponse(
            description = "description",
            code = "7100"
        ),
        debin = Transfer.Debin(
            id = "1",
            status = Transfer.DebinStatus(
                code = "EN CURSO",
                description = "en curso"
            ),
            date = ZonedDateTime.of(MIN, ZoneId.systemDefault()),
            expirationDate = ZonedDateTime.of(MAX, ZoneId.systemDefault())
        ),
        evaluate = Transfer.TransferEvaluate(
            score = "0",
            rules = "rules1, rules2"
        )
    )
