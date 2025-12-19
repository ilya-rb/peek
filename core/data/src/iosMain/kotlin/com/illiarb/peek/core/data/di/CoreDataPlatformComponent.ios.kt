package com.illiarb.peek.core.data.di

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.internal.DefaultKeyValueStorageFactory
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json

public actual interface CoreDataPlatformBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideKeyStorageFactory(json: Json): KeyValueStorage.Factory {
    return DefaultKeyValueStorageFactory(json)
  }
}