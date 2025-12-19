package com.illiarb.peek.features.reader

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
public object ReaderScreenBindings {

  @Provides
  @IntoSet
  public fun provideReaderScreenPresenterFactory(
    factory: ReaderScreenPresenterFactory
  ): Presenter.Factory = factory

  @Provides
  @IntoSet
  public fun provideReaderScreenFactory(factory: ReaderScreenFactory): Ui.Factory = factory
}