package com.illiarb.peek.features.home

import androidx.compose.runtime.Immutable
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.navigation.map.HomeScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
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
import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Duration

internal interface HomeScreenContract {

  @Immutable
  data class State(
    val articles: Async<ImmutableList<Article>>,
    val articlesLastUpdatedTime: Duration?,
    val newsSources: ImmutableList<NewsSourceKind>,
    val selectedNewsSourceIndex: Int,
    val articleSummaryToShow: Article?,
    val bookmarkMessage: BookmarkMessage?,
    val eventSink: (Event) -> Unit,
    val articlesEventSink: (ArticlesUi) -> Unit,
  ) : CircuitUiState {

    enum class BookmarkMessage {
      ADDED,
      REMOVED,
    }
  }

  data class ContentTriggers(
    val selectedNewsSourceIndex: Int,
    val articleBookmarked: Boolean,
    val manualReloadTriggered: Boolean,
  )

  sealed interface Event : CircuitUiEvent {
    data class SummaryResult(val result: SummaryScreen.Result) : Event
    data class TabClicked(val source: NewsSourceKind) : Event
    data object ErrorRetryClicked : Event
    data object SettingsClicked : Event
    data object BookmarksClicked : Event
    data object BookmarkToastResult : Event
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is HomeScreen) {
        ui<State> { state, modifier -> HomeScreen(state, modifier) }
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
      return if (screen is HomeScreen) {
        HomeScreenPresenter(navigator, peekApiService, messageDispatcher)
      } else {
        null
      }
    }
  }
}
