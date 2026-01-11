package com.illiarb.peek.features.home.bookmarks

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.uikit.messages.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.home.bookmarks.BookmarksScreenContract.State
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

  @Immutable
  data class State(
    val articles: Async<ImmutableList<Article>>,
    val articleSummaryToShow: Article?,
    val search: TextFieldState,
    val articlesEventSink: (ArticlesUi) -> Unit,
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
}

public interface BookmarksScreenComponent {

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
