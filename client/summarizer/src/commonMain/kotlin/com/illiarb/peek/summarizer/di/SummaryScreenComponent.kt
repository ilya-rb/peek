package com.illiarb.peek.summarizer.di

import com.illiarb.peek.summarizer.ui.internal.SummaryPresenterFactory
import com.illiarb.peek.summarizer.ui.internal.SummaryScreenFactory
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
public object SummaryScreenBindings {

  @Provides
  @IntoSet
  public fun bindSummaryScreenPresenterFactory(
    factory: SummaryPresenterFactory
  ): Presenter.Factory = factory

  @Provides
  @IntoSet
  public fun bindSummaryScreenUiFactory(factory: SummaryScreenFactory): Ui.Factory = factory
}