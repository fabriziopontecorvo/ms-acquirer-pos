package com.prismamp.todopago.util.tenant

import com.prismamp.todopago.commons.tenant.TenantSettings
import kotlinx.coroutines.CoroutineScope

interface FeatureToggle {
    fun getTenantSettings(): TenantSettings
    suspend fun <T> executeFeatureOrDefault(feature: String, default: T, block: suspend CoroutineScope.() -> T): T
}
