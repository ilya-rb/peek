package com.illiarb.peek.features.settings.di

import com.illiarb.peek.features.settings.SettingsScreenFactory
import com.illiarb.peek.features.settings.SettingsScreenPresenterFactory
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
public object SettingsScreenBindings {

  @Provides
  @IntoSet
  public fun provideSettingsScreenPresenterFactory(
    factory: SettingsScreenPresenterFactory
  ): Presenter.Factory = factory

  @Provides
  @IntoSet
  public fun provideSettingsScreenFactory(factory: SettingsScreenFactory): Ui.Factory = factory
}