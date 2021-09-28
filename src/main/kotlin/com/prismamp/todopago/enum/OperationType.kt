package com.prismamp.todopago.enum

enum class OperationType(val value: String, val readableName: String, val translateName: String) {
    LAPOS_PAYMENT("COMPRA_QR_ADQ", "pago", "PURCHASE"),
    INVALID("", "", "");

    companion object {
        private val map = values().associateBy(OperationType::value)

        @JvmStatic
        fun fromValue(value: String?) = map[value] ?: INVALID

        @JvmStatic
        fun fromReadableName(readableName: String?) = map[readableName] ?: INVALID

        fun translate(operationType: String) =
            fromValue(operationType).translateName.takeIf { it.isNotEmpty() } ?: operationType

    }
}
