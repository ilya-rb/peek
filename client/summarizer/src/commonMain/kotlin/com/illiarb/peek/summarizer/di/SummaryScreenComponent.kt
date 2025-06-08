package com.illiarb.peek.summarizer.di

import com.illiarb.peek.summarizer.ui.internal.SummaryPresenterFactory
import com.illiarb.peek.summarizer.ui.internal.SummaryScreenFactory
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

public interface SummaryScreenComponent {

  @Provides
  @IntoSet
  public fun bindSummaryScreenPresenterFactory(factory: SummaryPresenterFactory): Presenter.Factory =
    factory

  @Provides
  @IntoSet
  public fun bindSummaryScreenUiFactory(factory: SummaryScreenFactory): Ui.Factory = factory
}