package com.illiarb.peek.features.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.illiarb.peek.features.home.overlay.TagFilterContract
import com.illiarb.peek.summarizer.ui.SummaryScreen
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@CommonParcelize
public object HomeScreen : Screen, CommonParcelable {

  @Stable
  public data class State(
    val articles: Async<SnapshotStateList<Article>>,
    val newsSources: ImmutableList<NewsSourceKind>,
    val allTags: Set<Tag>,
    val selectedTags: ImmutableSet<Tag>,
    val selectedNewsSourceIndex: Int,
    val filtersShowing: Boolean,
    val articleSummaryToShow: Article?,
    val bookmarkMessage: BookmarkMessage?,
    val eventSink: (Event) -> Unit,
    val articlesEventSink: (ArticlesUiEvent) -> Unit,
  ) : CircuitUiState {

    public fun articlesStateKey(): Any {
      return when (articles) {
        is Async.Content -> articles.content.isEmpty()
        else -> this::class
      }
    }
  }

  public enum class BookmarkMessage {
    ADDED,
    REMOVED,
  }

  public sealed interface Event : CircuitUiEvent {
    public data class TagFilterResult(val result: TagFilterContract.Output) : Event
    public data class SummaryResult(val result: SummaryScreen.Result) : Event
    public data class TabClicked(val source: NewsSourceKind) : Event
    public data object ErrorRetryClicked : Event
    public data object FiltersClicked : Event
    public data object SettingsClicked : Event
    public data object BookmarksClicked : Event
    public data object BookmarkToastResult : Event
  }
}