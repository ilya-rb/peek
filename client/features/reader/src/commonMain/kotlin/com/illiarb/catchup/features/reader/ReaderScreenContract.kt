package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.service.domain.Article
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data class ReaderScreen(val articleId: String) : Screen, CommonParcelable {

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
}
