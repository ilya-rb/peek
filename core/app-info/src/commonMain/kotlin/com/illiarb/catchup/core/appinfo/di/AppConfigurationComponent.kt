package com.illiarb.catchup.core.appinfo.di

import com.illiarb.catchup.core.appinfo.AppConfiguration
import com.illiarb.catchup.core.appinfo.AppEnvironment
import com.illiarb.catchup.core.appinfo.BuildKonfig
import com.illiarb.catchup.core.appinfo.internal.DefaultAppConfiguration
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.KeyValueStorage
import me.tatarka.inject.annotations.Provides

public interface AppConfigurationComponent {

  public val keyValueStorage: KeyValueStorage
  public val appConfiguration: AppConfiguration

  @Provides
  public fun provideEnvironment(): AppEnvironment {
    return AppEnvironment.valueOf(BuildKonfig.ENV)
  }

  @Provides
  @AppScope
  public fun provideKeyValueStorage(
    factory: KeyValueStorage.Factory,
  ): KeyValueStorage {
    return factory.create(STORAGE_NAME)
  }

  @Provides
  @AppScope
  public fun provideAppConfiguration(
    environment: AppEnvironment,
    storage: KeyValueStorage,
  ): AppConfiguration {
    return DefaultAppConfiguration(environment, storage)
  }
}

private const val STORAGE_NAME = "app_config.storage"