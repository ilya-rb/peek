package com.illiarb.peek.di

import android.app.Activity
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.arch.message.MessageProvider
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(UiScope::class)
internal interface AndroidUiGraph : UiGraph {

  val messageDispatcher: MessageDispatcher
  val messageProvider: MessageProvider
  val circuit: Circuit

  @Multibinds
  fun presenterFactories(): Set<Presenter.Factory>

  @Multibinds
  fun uiFactories(): Set<Ui.Factory>

  @Provides
  @SingleIn(UiScope::class)
  fun provideCircuit(
    presenterFactories: Set<Presenter.Factory>,
    uiFactories: Set<Ui.Factory>,
  ): Circuit {
    return Circuit.Builder()
      .addPresenterFactories(presenterFactories)
      .addUiFactories(uiFactories)
      .build()
  }

  @GraphExtension.Factory
  fun interface Factory {
    fun create(@Provides activity: Activity): AndroidUiGraph
  }
}
