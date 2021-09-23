package com.prismamp.todopago.configuration.annotation

import org.springframework.stereotype.Service
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE

@Service
@Target(TYPE, CLASS)
@Retention(RUNTIME)
@MustBeDocumented
annotation class DomainService
