package com.illiarb.peek.features.summarizer.ui

import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.summarizer.SummarizerService
import com.illiarb.peek.features.summarizer.domain.ArticleSummary
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

internal interface SummaryScreenContract {

  data class State(
    val articleWithSummary: Async<ArticleWithSummary>,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState {

    data class ArticleWithSummary(
      val article: Article,
      val summary: ArticleSummary,
    )
  }

  sealed interface Event : CircuitUiEvent {
    data object NavigationIconClick : Event
    data class OpenInReaderClick(val article: Article) : Event
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is SummaryScreen) {
        ui<State> { state, modifier -> SummaryScreen(state, screen, modifier) }
      } else {
        null
      }
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  class PresenterFactory(
    private val peekApiService: PeekApiService,
    private val summarizerService: SummarizerService,
  ) : Presenter.Factory {

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is SummaryScreen) {
        SummaryScreenPresenter(navigator, screen, peekApiService, summarizerService)
      } else {
        null
      }
    }
  }
}