package com.illiarb.catchup.core.data

sealed class Async<out T> {

  data object Loading : Async<Nothing>()

  data class Content<T>(val content: T) : Async<T>()

  data class Error(val error: Throwable) : Async<Nothing>()
}
