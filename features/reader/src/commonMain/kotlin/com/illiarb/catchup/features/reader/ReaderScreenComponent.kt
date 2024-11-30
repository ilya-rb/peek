package com.illiarb.catchup.features.reader

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface ReaderScreenComponent {

  @Provides
  @IntoSet
  fun provideReaderScreenPresenterFactory(factory: ReaderScreenPresenter.Factory): Presenter.Factory = factory

  @Provides
  @IntoSet
  fun provideReaderScreenFactory(factory: Factory): Ui.Factory = factory
}