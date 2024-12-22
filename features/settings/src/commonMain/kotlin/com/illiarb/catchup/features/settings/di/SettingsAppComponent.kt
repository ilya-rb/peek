package com.illiarb.catchup.features.settings.di

import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.features.settings.data.DefaultSettingsService
import com.illiarb.catchup.features.settings.data.SettingsService
import me.tatarka.inject.annotations.Provides

public interface SettingsAppComponent {

  public val settingsService: SettingsService

  @AppScope
  @Provides
  public fun provideSettingsService(): SettingsService {
    return DefaultSettingsService()
  }
}