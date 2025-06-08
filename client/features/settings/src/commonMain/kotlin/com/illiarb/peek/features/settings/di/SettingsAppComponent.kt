package com.illiarb.peek.features.settings.di

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.features.settings.data.DefaultSettingsService
import com.illiarb.peek.features.settings.data.SettingsService
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