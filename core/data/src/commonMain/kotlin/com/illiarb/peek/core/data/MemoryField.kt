package com.illiarb.peek.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class MemoryField<T>(value: T) {

  private val current = MutableStateFlow(value)

  public fun get(): T = current.value

  public fun set(value: T) {
    current.value = value
  }

  public fun observe(): Flow<T> = current
}