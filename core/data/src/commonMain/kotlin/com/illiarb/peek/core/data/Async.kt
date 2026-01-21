package com.illiarb.peek.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

public sealed class Async<out T> {

  public data object Loading : Async<Nothing>()

  public data class Content<T>(
    val content: T,
    val contentRefreshing: Boolean,
    val suppressedError: Throwable? = null,
  ) : Async<T>()

  public data class Error(val error: Throwable) : Async<Nothing>()

  public fun contentOrNull(): T? {
    return when (this) {
      is Content -> content
      else -> null
    }
  }

  public fun errorOrNull(): Throwable? {
    return when (this) {
      is Error -> error
      else -> null
    }
  }

  public fun <R> map(mapper: (T) -> R): Async<R> {
    return when (this) {
      is Content -> Content(mapper(this.content), this.contentRefreshing, this.suppressedError)
      is Error -> Error(this.error)
      is Loading -> Loading
    }
  }

  public fun stateKey(): Any {
    return when (this) {
      is Content<*> -> {
        if (content is Collection<*>) {
          content.isEmpty()
        } else {
          this::class
        }
      }

      else -> this::class
    }
  }

  public fun <R> mergeWith(other: Async<R>): Async<Pair<T, R>> {
    return when {
      this is Loading || other is Loading -> Loading
      this is Content && other is Content -> {
        Content(
          content = this.content to other.content,
          contentRefreshing = this.contentRefreshing || other.contentRefreshing,
        )
      }

      else -> Error(requireNotNull(this.errorOrNull() ?: other.errorOrNull()))
    }
  }

  public companion object {

    public fun <T> fromFlow(value: suspend () -> T): Flow<Async<T>> {
      return flow<Async<T>> {
        emit(Content(value(), contentRefreshing = false))
      }.onStart {
        emit(Loading)
      }.catch { error ->
        emit(Error(error))
      }
    }
  }
}
