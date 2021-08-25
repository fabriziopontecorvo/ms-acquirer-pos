package com.prismamp.todopago.payment.adapter.command.model.exception

import com.prismamp.todopago.commons.rest.exception.LockedException

class LockedQrException(uniqueLockKey: String) : LockedException(
    "OPERATION_ALREADY_IN_PROCESS", "operacion bloqueada " +
            "porque ya existe una identica en proceso, lockKey: $uniqueLockKey"
)
