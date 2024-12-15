package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.Url
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data class ReaderScreen(val articleId: String) : Screen, CommonParcelable

internal interface ReaderScreenContract {

  @Stable
  data class State(
    val article: Async<Article>,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event {
    data object NavigationIconClicked : Event
    data class LinkClicked(val url: Url) : Event
    data object ErrorRetryClicked : Event
  }
}