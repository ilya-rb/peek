package com.illiarb.catchup.di

import com.illiarb.catchup.di.scope.ActivityScope
import com.illiarb.catchup.features.home.HomeScreenComponent
import com.illiarb.catchup.features.reader.ReaderScreenComponent
import com.illiarb.catchup.features.settings.di.SettingsScreenComponent
import com.illiarb.catchup.summarizer.di.SummaryScreenComponent
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.Provides

internal interface UiComponent :
  HomeScreenComponent,
  ReaderScreenComponent,
  SettingsScreenComponent,
  SummaryScreenComponent {

  val circuit: Circuit

  @ActivityScope
  @Provides
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
