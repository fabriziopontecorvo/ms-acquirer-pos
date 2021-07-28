package com.prismamp.todopago

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringBootApplication
class MsAcquirerPosApplication

fun main(args: Array<String>) {
	runApplication<MsAcquirerPosApplication>(*args)
}
