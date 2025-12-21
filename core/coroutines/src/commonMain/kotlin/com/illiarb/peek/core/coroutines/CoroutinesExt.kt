package com.illiarb.peek.core.coroutines

import kotlinx.coroutines.CancellationException

/**
 * suspend version of runCatching
 */
public suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
  return try {
    val result = block()
    Result.success(result)
  } catch (e: CancellationException) {
    throw e
  } catch (expected: Throwable) {
    Result.failure(expected)
  }
}
