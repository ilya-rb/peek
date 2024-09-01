package com.illiarb.catchup.features.home

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface HomeScreenComponent {

  @[Provides IntoSet]
  fun bindHomeScreenPresenterFactory(factory: HomeScreenPresenter.Factory): Presenter.Factory = factory

  @[Provides IntoSet]
  fun bindHomeScreenUiFactory(factory: Factory): Ui.Factory = factory
}
