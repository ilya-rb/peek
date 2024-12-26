package com.illiarb.catchup.features.settings.di

import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.KeyValueStorage
import com.illiarb.catchup.features.settings.data.DefaultSettingsService
import com.illiarb.catchup.features.settings.data.SettingsService
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Qualifier

public interface SettingsAppComponent {

  public val settingsService: SettingsService

  @SettingsStorage
  public val settingsStorage: KeyValueStorage

  @AppScope
  @Provides
  public fun provideSettingsService(
    @SettingsStorage storage: KeyValueStorage
  ): SettingsService {
    return DefaultSettingsService(storage)
  }

  @AppScope
  @SettingsStorage
  @Provides
  public fun provideSettingsStorage(factory: KeyValueStorage.Factory): KeyValueStorage {
    return factory.create(STORAGE_NAME)
  }
}

private const val STORAGE_NAME = "settings.storage"

@Qualifier
internal annotation class SettingsStorage