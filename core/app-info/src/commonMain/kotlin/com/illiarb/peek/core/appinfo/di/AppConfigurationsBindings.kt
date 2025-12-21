package com.illiarb.peek.core.appinfo.di

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.AppEnvironment
import com.illiarb.peek.core.appinfo.BuildKonfig
import com.illiarb.peek.core.appinfo.internal.DefaultAppConfiguration
import com.illiarb.peek.core.data.KeyValueStorage
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides

@BindingContainer
public abstract class AppConfigurationsBindings {

  @Binds
  internal abstract val DefaultAppConfiguration.bind: AppConfiguration

  internal companion object {

    @Provides
    fun provideEnvironment(): AppEnvironment {
      return AppEnvironment.valueOf(BuildKonfig.ENV)
    }

    @Provides
    fun provideKeyValueStorage(factory: KeyValueStorage.Factory): KeyValueStorage {
      return factory.create(STORAGE_NAME)
    }

    private const val STORAGE_NAME = "app_config.storage"
  }
}