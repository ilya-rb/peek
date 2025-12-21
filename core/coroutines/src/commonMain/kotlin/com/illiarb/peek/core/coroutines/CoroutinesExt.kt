package com.illiarb.peek.core.coroutines

import kotlinx.coroutines.CancellationException

/**
 * suspend version of runCatching
 */
public suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
  return try {
    val result = block()
    Result.success(result)
  } catch (e: Throwable) {
    if (e is CancellationException) {
      throw e
    } else {
      Result.failure(e)
    }
  }
}
