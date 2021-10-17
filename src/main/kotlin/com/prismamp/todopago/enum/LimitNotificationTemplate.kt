package com.prismamp.todopago.enum

enum class LimitNotificationTemplate(val notificationId: Long, val notificationBimoId: Long) {
    RISK_WARNING(70,159),
    RISK_REJECT(71,160),
    TP_WARNING(72,161),
    TP_REJECT(73,162),
    INVALID(0,0)
}
