package com.illiarb.catchup.features.home

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.filters.FiltersOverlayResult
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.uikit.core.model.Identifiable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@CommonParcelize
public object HomeScreen : Screen, CommonParcelable

internal interface HomeScreenContract {

  @Stable
  data class State(
    private val articles: Async<ImmutableList<Article>>,
    val tabs: Async<ImmutableList<Tab>>,
    val selectedTags: Set<Tag>,
    val selectedTabIndex: Int,
    val filtersShowing: Boolean,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState {

    val content: Async<ImmutableList<Article>>
      get() = when (articles) {
        is Async.Content -> {
          if (selectedTags.isEmpty()) {
            articles
          } else {
            articles.copy(
              articles.content.filter { article ->
                article.tags.any { tag ->
                  selectedTags.contains(tag)
                }
              }.toImmutableList()
            )
          }
        }

        else -> articles
      }

    val articleTags: Set<Tag>
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

  sealed interface Event : CircuitUiEvent {
    data object FiltersClicked : Event
    data object ErrorRetryClicked : Event
    data object SavedClicked : Event
    data object SettingsClicked : Event
    data class TabClicked(val source: NewsSource) : Event
    data class ArticleClicked(val item: Article) : Event
    data class FiltersResult(val result: FiltersOverlayResult) : Event
  }

  data class Tab(
    override val id: String,
    val imageUrl: String,
    val source: NewsSource,
  ) : Identifiable<String>
}
