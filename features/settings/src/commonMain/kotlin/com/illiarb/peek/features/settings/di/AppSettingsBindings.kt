package com.illiarb.peek.features.settings.di

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.features.settings.data.DefaultSettingsService
import com.illiarb.peek.features.settings.data.SettingsService
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn

@BindingContainer
public abstract class AppSettingsBindings {

  @Binds
  internal abstract val DefaultSettingsService.bind: SettingsService

  internal companion object {

    @Provides
    @SettingsStorage
    @SingleIn(AppScope::class)
    fun provideSettingsStorage(factory: KeyValueStorage.Factory): KeyValueStorage {
      return factory.create(STORAGE_NAME)
    }

    private const val STORAGE_NAME = "settings.storage"
  }
}

@Qualifier
internal annotation class SettingsStorage
