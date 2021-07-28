package com.prismamp.todopago.util.logs

import org.slf4j.Logger
import org.slf4j.MDC

data class BenchmarkResult<T>(val response: T, val elapsedTime: Long)


/**
 * It receives a block, executes it and returns the result of the block with the elapsed time.
 * @param block
 * @return @see BenchmarkResult<T>
 */
inline fun <T> measureTimeMillisWithResponse(block: () -> T): BenchmarkResult<T> {
  val start = System.currentTimeMillis()
  val response = block()
  return BenchmarkResult(response, System.currentTimeMillis() - start)
}

/**
 * Receives a block.
 * Logs the start with @param actionName and executes the block.
 * Logs the end and return block result.
 * @param actionName
 * @param block
 */
inline fun <T> Logger.benchmark(actionName: String, block: () -> T): T {
  info("Start execution of {}", actionName)
  val result = measureTimeMillisWithResponse(block)
  info("End execution of {} - elapsed time {} ms", actionName, result.elapsedTime)
  MDC.put("elapsed_time", result.elapsedTime.toString())
  return result.response
}

/**
 * Receives a logger and an exception to log a message containing class and error message
 * @param logger
 * @param e - an exception
 */
fun logWarn(logger: Logger, e: Throwable) {
  logger.warn("Exception: {}, Message: {}", e.javaClass, e.message)
}

/**
 * Receives a logger and an exception to log a message containing class and error message
 * @param logger
 * @param e - an exception
 */
fun logError(logger: Logger, e: Throwable) {
  logger.error("Exception: {} {} ", e.javaClass, e.stackTrace.joinToString { "-> $it" })
}
