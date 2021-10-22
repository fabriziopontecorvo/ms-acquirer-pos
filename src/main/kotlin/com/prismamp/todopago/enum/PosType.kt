package com.prismamp.todopago.enum

import java.util.*

enum class PosType(val value: String) {
    PAYSTORE("com.pp"),
    LAPOS("com.adq"),
    INVALID("");

    companion object {
        val map = values().associateBy(PosType::value)

        @JvmStatic
        fun from(value: String?, defaultType: PosType = INVALID) =
            map[value?.lowercase(Locale.getDefault())] ?: defaultType

        @JvmStatic
        fun from(value: String?, traceNumber: String, ticketNumber: String) =
            when (from(value)) {
                INVALID ->
                    takeIf { traceNumber == "9999" && ticketNumber == "9999" }
                        ?.let { PAYSTORE }
                        ?: LAPOS
                else -> from(value)
            }
    }
}
