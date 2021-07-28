package com.prismamp.todopago.configuration.annotation

import com.prismamp.todopago.commons.security.OAuth2TokenValidation
import com.prismamp.todopago.commons.security.OAuth2TokenValidatorArgument
import com.prismamp.todopago.commons.security.OAuth2TokenValidatorArgumentSource
import com.prismamp.todopago.commons.security.validator.JwtClaimAccountIdValidator

@OAuth2TokenValidation(
    validator = JwtClaimAccountIdValidator::class,
    arguments = [
        OAuth2TokenValidatorArgument(
            name = JwtClaimAccountIdValidator.ACCOUNT_ID_KEY,
            source = OAuth2TokenValidatorArgumentSource.REQUEST_BODY_ATTRIBUTE,
            value = "account_id"
        )
    ]
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OAuth2TokenBodyValidationAccountId
