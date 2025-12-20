package com.illiarb.peek.features.home.bookmarks

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@CommonParcelize
public object BookmarksScreen : Screen, CommonParcelable {

  @Stable
  internal data class State(
    val articles: Async<SnapshotStateList<Article>>,
    val articleSummaryToShow: Article?,
    val articlesEventSink: (ArticlesUiEvent) -> Unit,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState {

    fun articlesStateKey(): Any {
      return when (articles) {
        is Async.Content -> articles.content.isEmpty()
        else -> this::class
      }
    }
  }

  internal sealed interface Event {
    data object NavigationButtonClicked : Event
    data object ErrorRetryClicked : Event
    data object SummaryCloseClicked : Event
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class ScreenFactory : Ui.Factory {
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
  public class PresenterFactory(
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