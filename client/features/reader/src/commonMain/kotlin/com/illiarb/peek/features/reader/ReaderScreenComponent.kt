package com.illiarb.peek.features.reader

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

public interface ReaderScreenComponent {

  @Provides
  @IntoSet
  public fun provideReaderScreenPresenterFactory(factory: ReaderScreenPresenterFactory): Presenter.Factory =
    factory

  @Provides
  @IntoSet
  public fun provideReaderScreenFactory(factory: ReaderScreenFactory): Ui.Factory = factory
}