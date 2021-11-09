package com.prismamp.todopago.archunit

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

@AnalyzeClasses(packages = ["com.prismamp.todopago"], importOptions = [DoNotIncludeTests::class])
object LayeredArchitectureTest {

    private val DOMAIN = "Domain"
    private val ADAPTERS = "Adapters"
    private val APPLICATION = "Application"
    private val CONFIG = "Config"
    private val UTIL = "Util"

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
        .layer(CONFIG).definedBy("com.prismamp.todopago.configuration..")
        .layer(UTIL).definedBy("com.prismamp.todopago.util..")
        .layer(DOMAIN).definedBy("com.prismamp.todopago.payment.domain..")
        .layer(ADAPTERS).definedBy("com.prismamp.todopago.payment.adapter..")
        .layer(APPLICATION).definedBy("com.prismamp.todopago.payment.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG, UTIL)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG, UTIL)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG, UTIL)
}
