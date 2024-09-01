package com.illiarb.catchup.di

import com.illiarb.catchup.di.scope.ViewControllerScope
import com.illiarb.catchup.features.home.HomeScreenComponent
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.Provides

interface UiComponent : HomeScreenComponent {

  val circuit: Circuit

  @[Provides ViewControllerScope]
  fun provideCircuit(
    presenterFactories: Set<Presenter.Factory>,
    uiFactories: Set<Ui.Factory>,
  ): Circuit {
    return Circuit.Builder()
      .addPresenterFactories(presenterFactories)
      .addUiFactories(uiFactories)
      .build()
  }
}
