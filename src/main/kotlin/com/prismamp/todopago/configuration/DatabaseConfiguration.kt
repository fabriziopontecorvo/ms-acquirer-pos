
package com.prismamp.todopago.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DatabaseConfiguration {

    @Bean(name = ["dsMessages"])
    @ConfigurationProperties(prefix = "datasource.sql-server.messages")
    fun dataSourceMessages() = DataSourceBuilder.create().build()

}
