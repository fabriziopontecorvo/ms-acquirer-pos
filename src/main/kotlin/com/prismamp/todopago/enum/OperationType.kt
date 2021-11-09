package com.prismamp.todopago.enum

enum class OperationType(val value: String, val readableName: String, val translateName: String) {
    LAPOS_PAYMENT("COMPRA_QR_ADQ", "pago", "PURCHASE"),
    INVALID("", "", "");
}
