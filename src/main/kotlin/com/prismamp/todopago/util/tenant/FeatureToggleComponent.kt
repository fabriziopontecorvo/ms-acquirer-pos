package com.prismamp.todopago.util.tenant

import com.prismamp.todopago.commons.tenant.TenantHolder
import com.prismamp.todopago.commons.tenant.TenantSettings
import com.prismamp.todopago.util.logs.CompanionLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope


abstract class FeatureToggleComponent(
    private val tenantSettings: TenantSettings,
    tenantHolder: TenantHolder
) : FeatureToggle, TenantAwareComponent(tenantHolder) {

    companion object : CompanionLogger()

    override fun getTenantSettings() = tenantSettings

    override suspend fun <T> executeFeatureOrDefault(feature: String, default: T, block: suspend CoroutineScope.() -> T): T =
        feature
            .takeIf { tenantSettings.featureIsEnabled(it) }
            ?.log { info("executeFeatureOrDefault: the feature {} is enabled", feature) }
            ?.let { coroutineScope(block) }
            ?: default.log { warn("executeFeatureOrDefault: the feature {} is disabled", feature) }

}
