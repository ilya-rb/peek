package com.illiarb.peek.core.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

public fun <T, R> Flow<Async<T>>.mapContent(mapper: suspend (T) -> R): Flow<Async<R>> {
  return map { data ->
    when (data) {
      is Async.Loading -> Async.Loading
      is Async.Error -> Async.Error(data.error)
      is Async.Content -> {
        Async.Content(mapper(data.content), data.contentRefreshing)
      }
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
public fun <T, R> Flow<Async<T>>.flatMapLatestContent(
  mapper: (T) -> Flow<Async<R>>
): Flow<Async<R>> {
  return flatMapLatest { data ->
    when (data) {
      is Async.Content -> mapper(data.content)
      is Async.Error -> flowOf(Async.Error(data.error))
      is Async.Loading -> flowOf(Async.Loading)
    }
  }
}
