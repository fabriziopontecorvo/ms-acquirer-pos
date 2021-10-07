package com.prismamp.todopago.configuration.kafka

import com.prismamp.todopago.commons.queues.QueueKnownTenantsProvider
import com.prismamp.todopago.commons.queues.topic.TenantAwareConsumerTopicProvider
import com.prismamp.todopago.commons.queues.topic.TenantAwareTopicGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ConsumerConfiguration {

    @Bean
    fun tenantAwareConsumerTopicProvider(
        knownTenantsProvider: QueueKnownTenantsProvider
    ) = TenantAwareConsumerTopicProvider(knownTenantsProvider, TenantAwareTopicGenerator())
}
