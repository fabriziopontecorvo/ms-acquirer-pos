package com.prismamp.todopago.enum

enum class PaymentStatusRequest(name: String){
    PENDING("pending"),
    FAILURE("failure"),
    SUCCESS("success"),
    INVALID("");
}
