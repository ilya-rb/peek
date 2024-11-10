package com.illiarb.catchup.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

sealed class Async<out T> {

  data object Loading : Async<Nothing>()

  data class Content<T>(val content: T) : Async<T>()

  data class Error(val error: Throwable) : Async<Nothing>()

  companion object {

    fun <T> fromFlow(value: suspend () -> T): Flow<Async<T>> {
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
