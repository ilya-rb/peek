package com.illiarb.peek.core.data.ext

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.logging.catchWithLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

public fun <T> Flow<T>.asAsync(withLoading: Boolean = true): Flow<Async<T>> {
  return this.map {
    Async.Content(content = it, contentRefreshing = false) as Async<T>
  }.catchWithLog {
    emit(Async.Error(it))
  }.onStart {
    if (withLoading) {
      emit(Async.Loading)
    }
  }
}
