package com.illiarb.catchup.features.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.filters.ArticlesFilter
import com.illiarb.catchup.features.home.filters.FiltersContract
import com.illiarb.catchup.features.home.summary.SummaryOverlayContract
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import com.illiarb.catchup.uikit.core.model.Identifiable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList

@CommonParcelize
public object HomeScreen : Screen, CommonParcelable {

  @Stable
  internal data class State(
    private val articles: Async<SnapshotStateList<Article>>,
    val articlesFilter: ArticlesFilter.Composite,
    val tabs: Async<ImmutableList<Tab>>,
    val selectedTabIndex: Int,
    val filtersShowing: Boolean,
    val eventSink: (Event) -> Unit,
    val articleSummary: Async<SummarizedArticle>,
  ) : CircuitUiState {

    val content: Async<SnapshotStateList<Article>>
      get() = when (articles) {
        is Async.Content -> {
          articles.copy(
            content = articlesFilter.apply(articles.content).toMutableStateList()
          )
        }

        else -> articles
      }

    val articlesTags: Set<Tag>
      get() = when (articles) {
        is Async.Content -> {
          articles.content
            .map { it.tags }
            .flatten()
            .toSet()
        }

        else -> emptySet()
      }
  }

  internal sealed interface Event : CircuitUiEvent {
    data object FiltersClicked : Event
    data object ErrorRetryClicked : Event
    data object SettingsClicked : Event
    data class TabClicked(val source: NewsSource) : Event
    data class ArticleClicked(val item: Article) : Event
    data class ArticleBookmarkClicked(val item: Article) : Event
    data class ArticleSummarizeClicked(val item: Article) : Event
    data class FiltersResult(val result: FiltersContract.Result) : Event
    data class SummaryResult(val result: SummaryOverlayContract.Result) : Event
  }

  internal data class SummarizedArticle(
    val article: Article,
    val summary: ArticleSummary,
  )

  internal data class Tab(
    override val id: String,
    val imageUrl: String,
    val source: NewsSource,
  ) : Identifiable<String>
}
