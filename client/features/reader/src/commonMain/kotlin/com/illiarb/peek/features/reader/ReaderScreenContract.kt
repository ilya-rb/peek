package com.illiarb.peek.features.reader

import androidx.compose.runtime.Stable
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.types.Url
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data class ReaderScreen(val url: Url) : Screen, CommonParcelable {

  @Stable
  public data class State(
    val article: Async<Article>,
    val topBarPopupShowing: Boolean,
    val summaryShowing: Boolean,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  public sealed interface Event {

    public data object NavigationIconClicked : Event
    public data object TopBarMenuClicked : Event
    public data object TopBarMenuDismissed : Event
    public data object SummarizeCloseClicked : Event
    public data object ErrorRetryClicked : Event

    public data object TopBarOpenInBrowser : Event, TopBarMenuAction
    public data object TopBarSummarize : Event, TopBarMenuAction
    public data object TopBarShare : Event, TopBarMenuAction

    public interface TopBarMenuAction
  }
}
