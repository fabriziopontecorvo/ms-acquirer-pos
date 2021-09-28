package com.prismamp.todopago.configuration.decidir

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "micro-services.decidir")
@ConstructorBinding
data class DecidirApiKeyConfiguration(
        val apikey: HashMap<String, String>
)
