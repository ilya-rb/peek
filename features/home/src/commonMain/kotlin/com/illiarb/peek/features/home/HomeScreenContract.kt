package com.illiarb.peek.features.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.illiarb.peek.features.home.overlay.TagFilterContract
import com.illiarb.peek.summarizer.ui.SummaryScreen
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
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

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is HomeScreen) {
        ui<State> { state, modifier -> HomeScreen(state, modifier) }
      } else {
        null
      }
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class PresenterFactory(
    private val peekApiService: PeekApiService,
    private val messageDispatcher: MessageDispatcher,
  ) : Presenter.Factory {

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is HomeScreen) {
        HomeScreenPresenter(navigator, peekApiService, messageDispatcher)
      } else {
        null
      }
    }
  }
}