package com.illiarb.peek.features.reader

import androidx.compose.runtime.Stable
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.reader.ReaderScreenContract.State
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

internal interface ReaderScreenContract {

  @Stable
  data class State(
    val article: Async<Article>,
    val parsedContent: Async<HtmlContent?>,
    val showTopBarPopup: Boolean,
    val showSummary: Boolean,
    val showRemoveBookmarkConfirmation: Boolean,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event {

    data object NavigationIconClicked : Event
    data object TopBarMenuClicked : Event
    data object TopBarMenuDismissed : Event
    data object ErrorRetryClicked : Event
    data object SummarizeResult : Event
    data object ScrolledToEnd : Event
    data object TopBarOpenInBrowser : Event, TopBarMenuAction
    data object TopBarSummarize : Event, TopBarMenuAction
    data object TopBarShare : Event, TopBarMenuAction
    data class RemoveBookmarkResult(val remove: Boolean) : Event

    interface TopBarMenuAction
  }
}

public interface ReaderScreenComponent {

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is ReaderScreen) {
        ui<State> { state, modifier -> ReaderScreen(modifier, screen, state) }
      } else {
        null
      }
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class PresenterFactory(
    private val peekApiService: PeekApiService,
  ) : Presenter.Factory {

    private val articleContentParser = ArticleContentParser()

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is ReaderScreen) {
        ReaderScreenPresenter(navigator, screen, peekApiService, articleContentParser)
      } else {
        null
      }
    }
  }
}
