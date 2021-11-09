package com.prismamp.todopago.payment.adapter.command.model.exception

import com.prismamp.todopago.commons.rest.exception.LockedException

class LockedQrException(uniqueLockKey: String) : LockedException(
    "OPERATION_ALREADY_IN_PROCESS", "operation blocked because exist another one in progress, lockKey: $uniqueLockKey"
)
