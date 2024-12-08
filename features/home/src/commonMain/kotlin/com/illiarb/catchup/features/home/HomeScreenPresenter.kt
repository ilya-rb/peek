package com.illiarb.catchup.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.catchup.core.arch.OpenUrlScreen
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.mapContent
import com.illiarb.catchup.features.reader.ReaderScreenContract
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.Tag
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject

class HomeScreenPresenter(
  private val catchupService: CatchupService,
  private val navigator: Navigator,
) : Presenter<HomeScreenContract.State> {

  @Composable
  override fun present(): HomeScreenContract.State {
    var selectedTabIndex by rememberRetained { mutableStateOf(value = 0) }
    var debugMenuShowing by rememberRetained { mutableStateOf(false) }
    var filtersShowing by rememberRetained { mutableStateOf(false) }
    var reloadData by rememberRetained { mutableStateOf(false) }
    var selectedTags by rememberRetained { mutableStateOf(emptySet<Tag>()) }

    val sources by produceRetainedState<Async<ImmutableList<HomeScreenContract.Tab>>>(
      initialValue = Async.Loading,
      key1 = catchupService,
    ) {
      catchupService.collectAvailableSources().mapContent { sources ->
        sources.map { source ->
          HomeScreenContract.Tab(
            id = source.imageUrl.url,
            source = source,
            imageUrl = source.imageUrl.url,
          )
        }.toImmutableList()
      }.collect {
        value = it
      }
    }

    val articles by produceRetainedState<Async<ImmutableList<Article>>>(
      initialValue = Async.Loading,
      key1 = selectedTabIndex,
      key2 = sources,
      key3 = reloadData,
    ) {
      val source = when (val currentSources = sources) {
        is Async.Content -> currentSources.content.getOrNull(selectedTabIndex)
        else -> null
      }

      if (source == null) {
        value = Async.Loading
      } else {
        catchupService.collectLatestNewsFrom(source.source.kind)
          .mapContent { it.toImmutableList() }
          .collect { value = it }
      }
    }

    fun eventSink(event: HomeScreenContract.Event) {
      when (event) {
        is HomeScreenContract.Event.ButtonClick -> Unit
        is HomeScreenContract.Event.ErrorRetryClick -> reloadData = !reloadData
        is HomeScreenContract.Event.DebugMenuClick -> debugMenuShowing = true
        is HomeScreenContract.Event.FiltersClick -> filtersShowing = true
        is HomeScreenContract.Event.DebugMenuClosed -> debugMenuShowing = false
        is HomeScreenContract.Event.ArticleClicked -> {
          if (event.item.content != null) {
            navigator.goTo(ReaderScreenContract.ReaderScreen(event.item.id))
          } else {
            navigator.goTo(OpenUrlScreen(event.item.link.url))
          }
        }

        is HomeScreenContract.Event.TagsSelected -> {
          filtersShowing = false
          selectedTags = event.tags
        }

        is HomeScreenContract.Event.TabClicked -> {
          val value = sources
          require(value is Async.Content<ImmutableList<HomeScreenContract.Tab>>)

          selectedTabIndex = value.content
            .map { it.source }
            .indexOf(event.source)
        }
      }
    }

    return HomeScreenContract.State(
      articles = articles,
      tabs = sources,
      selectedTabIndex = selectedTabIndex,
      selectedTags = selectedTags,
      eventSink = ::eventSink,
      debugMenuShowing = debugMenuShowing,
      filtersShowing = filtersShowing,
    )
  }

  @Inject
  class Factory(
    private val catchupService: CatchupService,
  ) : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        is HomeScreenContract.HomeScreen -> HomeScreenPresenter(catchupService, navigator)
        else -> null
      }
    }
  }
}
