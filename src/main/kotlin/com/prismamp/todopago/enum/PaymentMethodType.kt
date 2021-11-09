package com.prismamp.todopago.enum

enum class PaymentMethodType(val value: String, val displayValue: String, val translatedDisplayValue: String) {
    CREDIT("CREDITO", "CRÉDITO", "CREDIT"),
    DEBIT("DEBITO", "DÉBITO", "DEBIT"),
    INVALID("", "", "");

    companion object {
        private val map = values().associateBy(PaymentMethodType::value)

        @JvmStatic
        fun from(value: String) = map[value] ?: INVALID

    }
}

