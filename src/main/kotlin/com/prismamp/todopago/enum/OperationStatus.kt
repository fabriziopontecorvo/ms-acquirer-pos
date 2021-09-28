package com.prismamp.todopago.enum

enum class OperationStatus(
    val value: String,
    val txStatusValue: String,
    val txStatusMessage: String,
    val translatedValue: String
) {
    APPROVED("APROBADA", "TX_APROBADA", "Aprobada", "APPROVED"),
    REJECTED("RECHAZADA", "TX_RECHAZADA", "Rechazada", "REJECTED"),
    PENDING("PENDIENTE", "TX_PENDIENTE", "Pendiente", "PENDING"),
    INVALID("", "", "", "");

    companion object {
        private val map = values().associateBy(OperationStatus::value)
        private val txStatusValueMap = values().associateBy(OperationStatus::txStatusValue)

        @JvmStatic
        fun from(value: String) = map[value] ?: INVALID
        fun fromTxStatusValue(txStatusValue: String) = txStatusValueMap[txStatusValue] ?: INVALID
        fun translate(operationStatus: String) =
            from(operationStatus).translatedValue.takeIf { it.isNotEmpty() } ?: operationStatus
    }
}
