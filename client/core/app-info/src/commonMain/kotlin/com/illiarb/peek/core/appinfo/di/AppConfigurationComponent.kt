package com.illiarb.peek.core.appinfo.di

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.AppEnvironment
import com.illiarb.peek.core.appinfo.BuildKonfig
import com.illiarb.peek.core.appinfo.internal.DefaultAppConfiguration
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
public object AppConfigurationsBindings {

  @Provides
  public fun provideEnvironment(): AppEnvironment {
    return AppEnvironment.valueOf(BuildKonfig.ENV)
  }

  @Provides
  public fun provideKeyValueStorage(
    factory: KeyValueStorage.Factory,
  ): KeyValueStorage {
    return factory.create(STORAGE_NAME)
  }

  @Provides
  @SingleIn(AppScope::class)
  public fun provideAppConfiguration(
    environment: AppEnvironment,
    storage: KeyValueStorage,
  ): AppConfiguration {
    return DefaultAppConfiguration(environment, storage)
  }
}

private const val STORAGE_NAME = "app_config.storage"