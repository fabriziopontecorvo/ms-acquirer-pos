package com.prismamp.todopago.paymentMethod

data class Bank(
        val id: Long = -1,
        val code: String = "",
        val name: String = "",
        val logo: String = ""
) {
    companion object {
        @JvmStatic
        fun longToCode(it: Long) = "000$it".takeLast(3)
    }
}
