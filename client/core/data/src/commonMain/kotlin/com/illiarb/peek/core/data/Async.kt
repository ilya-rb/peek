package com.illiarb.peek.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

public sealed class Async<out T> {

  public data object Loading : Async<Nothing>()

  public data class Content<T>(val content: T) : Async<T>()

  public data class Error(val error: Throwable) : Async<Nothing>()

  public fun contentOrNull(): T? {
    return when (this) {
      is Content -> content
      else -> null
    }
  }

  public companion object {

    public fun <T> fromFlow(value: suspend () -> T): Flow<Async<T>> {
      return flow<Async<T>> {
        emit(Content(value()))
      }.onStart {
        emit(Loading)
      }.catch { error ->
        emit(Error(error))
      }
    }
  }
}
