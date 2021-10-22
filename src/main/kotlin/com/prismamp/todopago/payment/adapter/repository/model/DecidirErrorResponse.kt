package com.prismamp.todopago.payment.adapter.repository.model

data class DecidirErrorResponse(
    val errorType: String = "",
    val validationErrors: List<Error> = emptyList()
){
    data class Error(
        val code: String = "",
        val param: String = ""
    )
}
