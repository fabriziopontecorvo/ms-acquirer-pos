package com.prismamp.todopago.configuration

import com.prismamp.todopago.commons.cache.TenantIdReceiver
import com.prismamp.todopago.commons.queues.QueueKnownTenantsProvider
import com.prismamp.todopago.commons.queues.QueueTenantReceiver
import com.prismamp.todopago.commons.rest.RequestLifecycleTenantEmitter
import com.prismamp.todopago.commons.rest.RequestTenantReceiver
import com.prismamp.todopago.commons.storage.RoutingTenantReceiver
import com.prismamp.todopago.commons.tenant.CurrentTenantIdProvider
import com.prismamp.todopago.commons.tenant.CurrentTenantIdReceiver
import com.prismamp.todopago.commons.tenant.TenantHolder
import com.prismamp.todopago.commons.tenant.TenantRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TenantSupportConfiguration {

    val errorTenant = "No existe un tenant en el contexto."

    @Bean
    fun requestTenantReceiver(provider: CurrentTenantIdProvider): RequestTenantReceiver =
        RequestTenantReceiver { provider.get() }

    @Bean
    fun requestTenantLifecycleEmitter(
        receiver: CurrentTenantIdReceiver,
        tenantHolder: TenantHolder
    ): RequestLifecycleTenantEmitter =
        object : RequestLifecycleTenantEmitter {
            override fun extracted(tenantCode: String?) {
                receiver.take(tenantCode!!)
            }

            override fun cleared() {
                tenantHolder.clear()
            }
        }

    @Bean
    fun tenantIdReceiver(currentTenantIdProvider: CurrentTenantIdProvider) =
        TenantIdReceiver { currentTenantIdProvider.get() ?: error(errorTenant) }


    @Bean
    fun queuesKnownTenantsProvider(tenantRepository: TenantRepository): QueueKnownTenantsProvider =
        QueueKnownTenantsProvider { tenantRepository.findAll().map { it.id } }

    @Bean
    fun queuesTenantReceiver(currentTenantIdProvider: CurrentTenantIdProvider): QueueTenantReceiver =
        QueueTenantReceiver { currentTenantIdProvider.get() ?: error(errorTenant) }

    @Bean
    fun routingTenantReceiver(currentTenantIdProvider: CurrentTenantIdProvider): RoutingTenantReceiver =
        RoutingTenantReceiver { currentTenantIdProvider.get() ?: error(errorTenant) }

}
