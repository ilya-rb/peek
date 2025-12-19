package com.illiarb.peek.features.home.bookmarks

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public object BookmarksScreen : Screen, CommonParcelable {

  @Stable
  public data class State(
    val articles: Async<SnapshotStateList<Article>>,
    val articleSummaryToShow: Article?,
    val articlesEventSink: (ArticlesUiEvent) -> Unit,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState {

    public fun articlesStateKey(): Any {
      return when (articles) {
        is Async.Content -> articles.content.isEmpty()
        else -> this::class
      }
    }
  }

  public sealed interface Event {
    public data object NavigationButtonClicked : Event
    public data object ErrorRetryClicked : Event
    public data object SummaryCloseClicked : Event
  }
}