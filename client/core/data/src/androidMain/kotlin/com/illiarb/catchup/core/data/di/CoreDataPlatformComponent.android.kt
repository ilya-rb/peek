package com.illiarb.catchup.core.data.di

import android.app.Application
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.KeyValueStorage
import com.illiarb.catchup.core.data.internal.DefaultKeyValueStorageFactory
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

public actual interface CoreDataPlatformComponent {

  @Provides
  @AppScope
  public fun provideKeyStorageFactory(
    application: Application,
    json: Json,
  ): KeyValueStorage.Factory {
    return DefaultKeyValueStorageFactory(application, json)
  }
}