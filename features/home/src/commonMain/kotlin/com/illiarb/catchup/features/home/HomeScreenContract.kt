package com.illiarb.catchup.features.home

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.uikit.core.model.Identifiable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList

interface HomeScreenContract {

  @CommonParcelize
  object HomeScreen : Screen, CommonParcelable

  @Stable
  data class State(
    val articles: Async<ImmutableList<Article>>,
    val tabs: Async<ImmutableList<Tab>>,
    val selectedTabIndex: Int,
    val debugMenuShowing: Boolean,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object ButtonClick : Event
    data object DebugMenuClick : Event
    data object DebugMenuClosed : Event
    data object ErrorRetryClick : Event
    data class TabClicked(val source: NewsSource) : Event
    data class ArticleClicked(val item: Article) : Event
  }

  data class Tab(
    override val id: String,
    val imageUrl: String,
    val source: NewsSource,
  ) : Identifiable<String>
}
