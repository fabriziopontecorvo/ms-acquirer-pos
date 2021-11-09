package com.prismamp.todopago.configuration.http

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class IdleConnectionMonitorThread(
    private val connMgr: PoolingHttpClientConnectionManager
) : Thread() {

    @Volatile
    private var shutdown = false
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    override fun run() {
        try {
            while (!shutdown) {
                lock.withLock {
                    condition.await(1, SECONDS)
                    connMgr.closeExpiredConnections()
                    connMgr.closeIdleConnections(30, SECONDS)
                }
            }
        } catch (ex: InterruptedException) {
            shutdown()
        }
    }

    private fun shutdown() {
        shutdown = true
        lock.withLock {
            condition.signalAll()
        }
    }
}
