package com.prismamp.todopago.util.tenant

fun interface TenantAware {
    fun getTenant(): String
}
