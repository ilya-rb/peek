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
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@CommonParcelize
public object HomeScreen : Screen, CommonParcelable {

  @Stable
  internal data class State(
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

    fun articlesStateKey(): Any {
      return when (articles) {
        is Async.Content -> articles.content.isEmpty()
        else -> this::class
      }
    }
  }

  internal enum class BookmarkMessage {
    ADDED,
    REMOVED,
  }

  internal sealed interface Event : CircuitUiEvent {
    data class TagFilterResult(val result: TagFilterContract.Output) : Event
    data class SummaryResult(val result: SummaryScreen.Result) : Event
    data class TabClicked(val source: NewsSourceKind) : Event
    data object ErrorRetryClicked : Event
    data object FiltersClicked : Event
    data object SettingsClicked : Event
    data object BookmarksClicked : Event
    data object BookmarkToastResult : Event
  }
}

@BindingContainer
public object HomeScreenBindings {

  @Provides
  @IntoSet
  public fun bindHomeScreenPresenterFactory(factory: HomeScreenPresenterFactory): Presenter.Factory =
    factory

  @Provides
  @IntoSet
  public fun bindHomeScreenUiFactory(factory: HomeScreenFactory): Ui.Factory = factory
}
