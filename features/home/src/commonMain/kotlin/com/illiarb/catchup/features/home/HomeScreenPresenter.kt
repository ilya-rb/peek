package com.illiarb.catchup.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.mapContent
import com.illiarb.catchup.features.home.HomeScreen.Event
import com.illiarb.catchup.features.home.filters.ArticlesFilter
import com.illiarb.catchup.features.home.filters.FiltersContract
import com.illiarb.catchup.features.reader.ReaderScreen
import com.illiarb.catchup.features.settings.SettingsScreen
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.summarizer.SummarizerService
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
public class HomeScreenPresenterFactory(
  private val catchupService: CatchupService,
  private val summarizerService: SummarizerService,
) : Presenter.Factory {
  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is HomeScreen -> HomeScreenPresenter(catchupService, summarizerService, navigator)
      else -> null
    }
  }
}

internal class HomeScreenPresenter(
  private val catchupService: CatchupService,
  private val summarizerService: SummarizerService,
  private val navigator: Navigator,
) : Presenter<HomeScreen.State> {

  @Composable
  override fun present(): HomeScreen.State {
    val coroutineScope = rememberStableCoroutineScope()

    var selectedTabIndex by rememberRetained { mutableStateOf(value = 0) }
    var filtersShowing by rememberRetained { mutableStateOf(value = false) }
    var articlesFilter by rememberRetained {
      mutableStateOf(ArticlesFilter.Composite(filters = emptySet()))
    }

    var articleSummary by rememberRetained {
      mutableStateOf<Async<ArticleSummary>>(value = Async.Loading)
    }

    val sources by produceRetainedState<Async<ImmutableList<HomeScreen.Tab>>>(
      initialValue = Async.Loading,
      key1 = catchupService,
    ) {
      catchupService.collectAvailableSources().mapContent { sources ->
        sources.map { source ->
          HomeScreen.Tab(
            id = source.kind.name,
            source = source,
            imageUrl = source.imageUrl.url,
          )
        }.toImmutableList()
      }.collect {
        value = it
      }
    }

    var manualTriggers by rememberRetained {
      mutableStateOf(
        ManualTriggers(
          forceReload = false,
          articleSaved = false,
        )
      )
    }

    val articles by produceRetainedState<Async<SnapshotStateList<Article>>>(
      initialValue = Async.Loading,
      key1 = selectedTabIndex,
      key2 = sources,
      key3 = manualTriggers,
    ) {
      val source = when (val currentSources = sources) {
        is Async.Content -> currentSources.content.getOrNull(selectedTabIndex)
        else -> null
      }

      if (source == null) {
        value = Async.Loading
      } else {
        catchupService.collectLatestNewsFrom(source.source.kind)
          .mapContent { it.toMutableStateList() }
          .collect { value = it }
      }
    }

    fun eventSink(event: Event) {
      when (event) {
        is Event.SettingsClicked -> navigator.goTo(SettingsScreen)
        is Event.ErrorRetryClicked -> {
          manualTriggers = manualTriggers.copy(
            forceReload = !manualTriggers.forceReload
          )
        }

        is Event.FiltersClicked -> filtersShowing = true
        is Event.ArticleClicked -> navigator.goTo(ReaderScreen(event.item.id))

        is Event.ArticleBookmarkClicked -> {
          coroutineScope.launch {
            catchupService.saveArticle(event.item.copy(saved = !event.item.saved))
              .onSuccess {
                manualTriggers = manualTriggers.copy(articleSaved = !manualTriggers.articleSaved)
              }
          }
        }

        is Event.ArticleSummarizeClicked -> coroutineScope.launch {
          val summary = summarizerService.summarizeArticle(event.item.link.url)
            .filter { it !is Async.Loading }
            .first()

          articleSummary = summary
        }

        is Event.SummaryResult -> {
          articleSummary = Async.Loading
        }

        is Event.FiltersResult -> {
          if (event.result is FiltersContract.Result.Saved) {
            articlesFilter = event.result.filter
          }
          filtersShowing = false
        }

        is Event.TabClicked -> {
          val value = sources
          require(value is Async.Content<ImmutableList<HomeScreen.Tab>>)

          selectedTabIndex = value.content
            .map { it.source }
            .indexOf(event.source)
        }
      }
    }

    return HomeScreen.State(
      articles = articles,
      tabs = sources,
      selectedTabIndex = selectedTabIndex,
      eventSink = ::eventSink,
      filtersShowing = filtersShowing,
      articlesFilter = articlesFilter,
      articleSummary = articleSummary,
    )
  }

  data class ManualTriggers(
    val forceReload: Boolean,
    val articleSaved: Boolean,
  )
}
