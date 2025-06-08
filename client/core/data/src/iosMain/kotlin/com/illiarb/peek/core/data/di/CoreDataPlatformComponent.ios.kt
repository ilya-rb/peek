package com.illiarb.peek.core.data.di

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.internal.DefaultKeyValueStorageFactory
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

public actual interface CoreDataPlatformComponent {

  @Provides
  @AppScope
  public fun provideKeyStorageFactory(json: Json): KeyValueStorage.Factory {
    return DefaultKeyValueStorageFactory(json)
  }
}