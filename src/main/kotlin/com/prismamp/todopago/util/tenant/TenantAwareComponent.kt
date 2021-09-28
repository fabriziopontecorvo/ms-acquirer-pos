package com.prismamp.todopago.util.tenant

import com.prismamp.todopago.commons.tenant.Tenant
import com.prismamp.todopago.commons.tenant.TenantHolder
import org.springframework.stereotype.Component

@Component
class TenantAwareComponent(
    private val tenantHolder: TenantHolder
) {

    fun getCurrentTenant() =
        TenantAware { tenantHolder.getCurrent()?.id!!.lowercase() }.getTenant()

    fun setCurrentTenant(value: String) =
        tenantHolder.setCurrent(Tenant.Known(value))
}
