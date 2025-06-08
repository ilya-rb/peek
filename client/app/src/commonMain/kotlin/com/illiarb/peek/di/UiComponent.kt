package com.illiarb.peek.di

import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.di.scope.ActivityScope
import com.illiarb.peek.features.home.HomeScreenComponent
import com.illiarb.peek.features.home.bookmarks.BookmarksScreenComponent
import com.illiarb.peek.features.reader.ReaderScreenComponent
import com.illiarb.peek.features.settings.di.SettingsScreenComponent
import com.illiarb.peek.message.ToastMessageDispatcher
import com.illiarb.peek.summarizer.di.SummaryScreenComponent
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.Provides

internal interface UiComponent :
  HomeScreenComponent,
  ReaderScreenComponent,
  SettingsScreenComponent,
  SummaryScreenComponent,
  BookmarksScreenComponent {

  val circuit: Circuit
  val toastMessageDispatcher: ToastMessageDispatcher

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

  @ActivityScope
  @Provides
  fun provideToastMessageDispatcher(): ToastMessageDispatcher {
    return ToastMessageDispatcher()
  }

  @Provides
  fun provideMessageDispatcher(dispatcher: ToastMessageDispatcher): MessageDispatcher {
    return dispatcher
  }
}
