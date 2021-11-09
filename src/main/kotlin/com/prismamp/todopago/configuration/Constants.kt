package com.prismamp.todopago.configuration

import org.springframework.beans.factory.annotation.Value

class Constants {

    companion object {
        @Value("\${spring.application.name}")
        var APP_NAME: String = "ms-acquirer-pos"

        const val MS_ACCOUNT = "ms-account"
        const val MS_LIMIT = "ms-limit"
        const val MS_PAYMENT_METHODS = "ms-payment-methods"
        const val MS_ADQUIRENTE_PERSISTENCE = "ms-adquirente-persistence"
        const val MS_ACQUIRER_BENEFIT = "ms-acquirer-benefit"
        const val DECIDIR = "decidir"
        const val TP_TENANT: String = "tp"
        const val BIMO_TENANT: String = "bimo"
    }

}
