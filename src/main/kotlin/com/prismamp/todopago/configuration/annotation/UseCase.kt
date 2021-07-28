package com.prismamp.todopago.configuration.annotation

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE


@Component
@Target(TYPE, CLASS)
@Retention(RUNTIME)
@MustBeDocumented
annotation class UseCase
