package com.illiarb.peek.features.home.bookmarks

import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.illiarb.peek.features.navigation.map.BookmarksScreen
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList

internal interface BookmarksScreenContract {

  data class State(
    val articles: Async<ImmutableList<Article>>,
    val articleSummaryToShow: Article?,
    val articlesEventSink: (ArticlesUiEvent) -> Unit,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event {
    data object NavigationButtonClicked : Event
    data object ErrorRetryClicked : Event
    data object SummaryCloseClicked : Event
  }

  data class ContentTriggers(
    val articleBookmarked: Boolean,
    val contentForceRefresh: Boolean,
  )

  @Inject
  @ContributesIntoSet(UiScope::class)
  class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is BookmarksScreen) {
        ui<State> { state, modifier -> BookmarksScreen(state, modifier) }
      } else {
        null
      }
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  class PresenterFactory(
    private val peekApiService: PeekApiService,
    private val messageDispatcher: MessageDispatcher,
  ) : Presenter.Factory {

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is BookmarksScreen) {
        BookmarksPresenter(navigator, peekApiService, messageDispatcher)
      } else {
        null
      }
    }
  }
}