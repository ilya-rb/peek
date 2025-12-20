package com.illiarb.peek.features.reader

import androidx.compose.runtime.Stable
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.types.Url
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
public data class ReaderScreen(val url: Url) : Screen, CommonParcelable {

  @Stable
  internal data class State(
    val article: Async<Article>,
    val topBarPopupShowing: Boolean,
    val summaryShowing: Boolean,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  internal sealed interface Event {

    data object NavigationIconClicked : Event
    data object TopBarMenuClicked : Event
    data object TopBarMenuDismissed : Event
    data object SummarizeCloseClicked : Event
    data object ErrorRetryClicked : Event

    data object TopBarOpenInBrowser : Event, TopBarMenuAction
    data object TopBarSummarize : Event, TopBarMenuAction
    data object TopBarShare : Event, TopBarMenuAction

    interface TopBarMenuAction
  }

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

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is ReaderScreen) {
        ReaderScreenPresenter(navigator, screen, peekApiService)
      } else {
        null
      }
    }
  }
}
