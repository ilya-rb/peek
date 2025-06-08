package com.illiarb.catchup.features.home.bookmarks

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.articles.ArticlesUiEvent
import com.illiarb.catchup.service.domain.Article
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

@CommonParcelize
internal object BookmarksScreen : Screen, CommonParcelable {

  @Stable
  data class State(
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

  sealed interface Event {
    data object NavigationButtonClicked : Event
    data object ErrorRetryClicked : Event
    data object SummaryCloseClicked : Event
  }
}

public interface BookmarksScreenComponent {

  @[Provides IntoSet]
  public fun bindBookmarksScreenFactory(factory: BookmarksScreenFactory): Ui.Factory = factory

  @[Provides IntoSet]
  public fun bindBookmarksPresenterFactory(factory: BookmarksScreenPresenterFactory): Presenter.Factory =
    factory
}