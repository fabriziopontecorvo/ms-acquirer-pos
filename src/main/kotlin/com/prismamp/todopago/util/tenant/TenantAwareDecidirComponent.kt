package com.prismamp.todopago.util.tenant

import com.prismamp.todopago.commons.rest.exception.NotFoundException
import com.prismamp.todopago.configuration.decidir.DecidirApiKeyConfiguration
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class TenantAwareDecidirComponent(
    private val tenantAwareComponent: TenantAwareComponent,
    private val decidirApiKeyConfiguration: DecidirApiKeyConfiguration
) {

    fun buildEntity(body: Any? = null): HttpEntity<Any> {
        return body
            ?.let { HttpEntity(it, headersWithApiKey()) }
            ?: HttpEntity(headersWithApiKey())
    }

    private fun headersWithApiKey() =
        HttpHeaders().apply {
            add("apikey", apiKey())
        }

    private fun apiKey() =
        tenantAwareComponent.getCurrentTenant()
            .let { tenant ->
                decidirApiKeyConfiguration.apikey[tenant] ?: throw NotFoundException(tenant)
            }

}
