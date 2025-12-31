package com.illiarb.peek.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

public interface KeyValueStorage {

  public suspend fun <T> put(key: String, value: T, serializer: KSerializer<T>): Result<Unit>

  public suspend fun <T> get(key: String, serializer: KSerializer<T>): Result<T?>

  public fun <T> observe(key: String, serializer: KSerializer<T>): Flow<T>

  public interface Factory {

    public fun create(storageName: String): KeyValueStorage
  }
}
