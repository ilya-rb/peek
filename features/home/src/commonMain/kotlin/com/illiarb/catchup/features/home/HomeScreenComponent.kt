package com.illiarb.catchup.features.home

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

public interface HomeScreenComponent {

  @Provides
  @IntoSet
  public fun bindHomeScreenPresenterFactory(factory: HomeScreenPresenterFactory): Presenter.Factory = factory

  @Provides
  @IntoSet
  public fun bindHomeScreenUiFactory(factory: HomeScreenFactory): Ui.Factory = factory
}
