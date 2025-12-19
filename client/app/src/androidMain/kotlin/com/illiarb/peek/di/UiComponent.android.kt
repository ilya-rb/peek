package com.illiarb.peek.di

import android.app.Activity
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.di.scope.ActivityScope
import com.illiarb.peek.features.home.HomeScreenBindings
import com.illiarb.peek.features.home.bookmarks.BookmarksScreenBindings
import com.illiarb.peek.features.reader.ReaderScreenBindings
import com.illiarb.peek.features.settings.di.SettingsScreenBindings
import com.illiarb.peek.message.ToastMessageDispatcher
import com.illiarb.peek.summarizer.di.SummaryScreenBindings
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(
  scope = ActivityScope::class,
  bindingContainers = [
    BookmarksScreenBindings::class,
    HomeScreenBindings::class,
    ReaderScreenBindings::class,
    SettingsScreenBindings::class,
    SummaryScreenBindings::class,
  ]
)
internal interface AndroidUiGraph : UiGraph {

  val circuit: Circuit
  val toastMessageDispatcher: ToastMessageDispatcher

  @GraphExtension.Factory
  fun interface Factory {
    fun create(@Provides activity: Activity): AndroidUiGraph
  }

  @Provides
  @SingleIn(ActivityScope::class)
  fun provideCircuit(
    presenterFactories: Set<Presenter.Factory>,
    uiFactories: Set<Ui.Factory>,
  ): Circuit {
    return Circuit.Builder()
      .addPresenterFactories(presenterFactories)
      .addUiFactories(uiFactories)
      .build()
  }

  @Provides
  @SingleIn(ActivityScope::class)
  fun provideToastMessageDispatcher(): ToastMessageDispatcher {
    return ToastMessageDispatcher()
  }

  @Provides
  fun provideMessageDispatcher(dispatcher: ToastMessageDispatcher): MessageDispatcher {
    return dispatcher
  }
}
