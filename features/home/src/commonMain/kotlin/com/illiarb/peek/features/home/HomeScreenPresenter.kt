package com.illiarb.peek.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.home.HomeScreenContract.ArticlesResult
import com.illiarb.peek.features.home.HomeScreenContract.Event
import com.illiarb.peek.features.home.HomeScreenContract.NewsSource
import com.illiarb.peek.features.home.HomeScreenContract.TasksIndicator
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.navigation.map.BookmarksScreen
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.SettingsScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.navigation.map.TasksScreen
import com.illiarb.peek.features.tasks.TasksService
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.uikit.messages.Message
import com.illiarb.peek.uikit.messages.MessageDispatcher
import com.illiarb.peek.uikit.messages.MessageType
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.bookmarks_action_removed
import com.illiarb.peek.uikit.resources.bookmarks_action_saved
import com.illiarb.peek.uikit.resources.dou_logo
import com.illiarb.peek.uikit.resources.ft_logo
import com.illiarb.peek.uikit.resources.hn_logo
import com.illiarb.peek.uikit.resources.service_dou_name
import com.illiarb.peek.uikit.resources.service_ft_name
import com.illiarb.peek.uikit.resources.service_hacker_news_name
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

internal class HomeScreenPresenter(
  private val navigator: Navigator,
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
  private val tasksService: TasksService,
) : Presenter<HomeScreenContract.State> {

  @Composable
  @Suppress("CyclomaticComplexMethod", "LongMethod")
  override fun present(): HomeScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()
    val today = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .date

    val tasksIndicator by produceRetainedState<TasksIndicator>(
      initialValue = TasksIndicator.None,
      key1 = today,
    ) {
      tasksService.getTasksForDate(today).collect { async ->
        value = when (async) {
          is Async.Loading -> TasksIndicator.None
          is Async.Error -> TasksIndicator.None
          is Async.Content -> {
            val anytimeTasks = async.content.filter { it.timeOfDay == TimeOfDay.ANYTIME }
            when {
              anytimeTasks.isEmpty() -> TasksIndicator.None
              anytimeTasks.all { it.completed } -> TasksIndicator.AllTasksCompleted
              else -> TasksIndicator.HasIncompleteTasks
            }
          }
        }
      }
    }

    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }
    var servicesOrderToShow by rememberRetained {
      mutableStateOf<Unit?>(null)
    }
    var contentTriggers by rememberRetained {
      mutableStateOf(
        HomeScreenContract.ContentTriggers(
          selectedNewsSourceIndex = -1,
          articleBookmarked = false,
          manualReloadTriggered = false,
        )
      )
    }
    var contentRefreshing by rememberRetained {
      mutableStateOf(false)
    }

    val newsSources by produceRetainedState(emptyList<NewsSource>().toImmutableList()) {
      peekApiService.collectAvailableSources()
        .mapContent { sources ->
          sources.map { source ->
            NewsSource(
              kind = source.kind,
              name = source.kind.name(),
              icon = source.kind.icon(),
            )
          }
        }
        .collect {
          if (it is Async.Content) {
            val existingIndex = contentTriggers.selectedNewsSourceIndex
            val newValue = it.content.toImmutableList()

            contentTriggers = if (existingIndex == -1) {
              contentTriggers.copy(selectedNewsSourceIndex = 0)
            } else {
              contentTriggers.copy(
                selectedNewsSourceIndex = newValue.indexOf(value[existingIndex])
              )
            }
            value = newValue
          }
        }
    }

    val articles by produceRetainedState(
      initialValue = ArticlesResult(
        articles = Async.Loading,
        lastUpdated = null
      ),
      key1 = contentTriggers,
    ) {
      val source = newsSources.getOrNull(contentTriggers.selectedNewsSourceIndex)
      if (source != null) {
        peekApiService.collectLatestNewsFrom(
          kind = source.kind,
          strategy = if (contentRefreshing) {
            AsyncDataStore.LoadStrategy.ForceReload
          } else {
            null
          }
        ).collect { async ->
          contentRefreshing = if (async is Async.Content<*>) {
            async.contentRefreshing
          } else {
            false
          }
          value = when (async) {
            is Async.Loading -> ArticlesResult(Async.Loading, null)
            is Async.Error -> ArticlesResult(Async.Error(async.error), null)
            is Async.Content -> ArticlesResult(
              articles = Async.Content(
                content = async.content.articles.toImmutableList(),
                contentRefreshing = async.contentRefreshing,
                suppressedError = async.suppressedError,
              ),
              lastUpdated = async.content.lastUpdated,
            )
          }
        }
      }
    }

    return HomeScreenContract.State(
      newsSources = newsSources,
      selectedNewsSourceIndex = contentTriggers.selectedNewsSourceIndex,
      articleSummaryToShow = articleSummaryToShow,
      articlesLastUpdatedTime = articles.lastUpdated,
      contentRefreshing = contentRefreshing,
      servicesOrderToShow = servicesOrderToShow,
      articles = articles.articles,
      tasksIndicator = tasksIndicator,
      eventSink = { event ->
        when (event) {
          is Event.SettingsClicked -> navigator.goTo(SettingsScreen)
          is Event.ErrorRetryClicked -> {
            contentTriggers = contentTriggers.copy(
              manualReloadTriggered = !contentTriggers.manualReloadTriggered
            )
          }

          is Event.ReorderServicesClicked -> {
            servicesOrderToShow = Unit
          }

          is Event.ReorderServicesClosed -> {
            servicesOrderToShow = null
          }

          is Event.SummaryResult -> {
            articleSummaryToShow = null

            when (event.result) {
              is SummaryScreen.Result.Close -> Unit
              is SummaryScreen.Result.OpenInReader -> {
                navigator.goTo(ReaderScreen(event.result.url))
              }
            }
          }

          is Event.TabClicked -> {
            contentTriggers = contentTriggers.copy(
              selectedNewsSourceIndex = newsSources.indexOf(event.source)
            )
          }

          is Event.BookmarksClicked -> {
            navigator.goTo(BookmarksScreen)
          }

          is Event.TasksClicked -> {
            navigator.goTo(TasksScreen)
          }

          is Event.RefreshTriggered -> {
            contentRefreshing = true
            contentTriggers = contentTriggers.copy(
              manualReloadTriggered = !contentTriggers.manualReloadTriggered
            )
          }
        }
      },
      articlesEventSink = { event ->
        when (event) {
          is ArticlesUi.ArticleClicked -> {
            navigator.goTo(ReaderScreen(event.item.url))
          }

          is ArticlesUi.ArticleBookmarkClicked -> {
            coroutineScope.launch {
              val saved = !event.item.saved

              peekApiService.saveArticle(event.item.copy(saved = saved))
                .onSuccess {
                  contentTriggers = contentTriggers.copy(
                    articleBookmarked = !contentTriggers.articleBookmarked
                  )
                  val message = if (saved) {
                    getString(Res.string.bookmarks_action_saved)
                  } else {
                    getString(Res.string.bookmarks_action_removed)
                  }

                  messageDispatcher.sendMessage(
                    Message(
                      content = message,
                      type = MessageType.SUCCESS,
                    )
                  )
                }
            }
          }

          is ArticlesUi.ArticleSummarizeClicked -> {
            articleSummaryToShow = event.item
          }

          is ArticlesUi.ArticleShareClicked -> {
            navigator.goTo(ShareScreen(event.item.url))
          }

          is ArticlesUi.ArticlesRefreshClicked -> {
            contentTriggers = contentTriggers.copy(
              manualReloadTriggered = !contentTriggers.manualReloadTriggered
            )
          }
        }
      }
    )
  }

  private fun NewsSourceKind.icon(): DrawableResource {
    return when (this) {
      NewsSourceKind.HackerNews -> Res.drawable.hn_logo
      NewsSourceKind.Dou -> Res.drawable.dou_logo
      NewsSourceKind.Ft -> Res.drawable.ft_logo
    }
  }

  private suspend fun NewsSourceKind.name(): String {
    return getString(
      when (this) {
        NewsSourceKind.HackerNews -> Res.string.service_hacker_news_name
        NewsSourceKind.Dou -> Res.string.service_dou_name
        NewsSourceKind.Ft -> Res.string.service_ft_name
      }
    )
  }
}
