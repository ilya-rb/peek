package com.illiarb.peek.core.logging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch

public fun <T> Flow<T>.catchWithLog(block: suspend FlowCollector<T>.(Throwable) -> Unit): Flow<T> {
  return this.catch { error ->
    Logger.e(throwable = error)

    block(this, error)
  }
}
