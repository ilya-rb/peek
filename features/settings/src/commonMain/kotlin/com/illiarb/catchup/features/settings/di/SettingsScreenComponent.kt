package com.illiarb.catchup.features.settings.di

import com.illiarb.catchup.features.settings.SettingsScreenFactory
import com.illiarb.catchup.features.settings.SettingsScreenPresenterFactory
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

public interface SettingsScreenComponent {

  @Provides
  @IntoSet
  public fun provideSettingsScreenPresenterFactory(factory: SettingsScreenPresenterFactory): Presenter.Factory = factory

  @Provides
  @IntoSet
  public fun provideSettingsScreenFactory(factory: SettingsScreenFactory): Ui.Factory = factory
}