package com.illiarb.peek.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.features.home.HomeScreenContract.ArticlesResult
import com.illiarb.peek.features.home.HomeScreenContract.Event
import com.illiarb.peek.features.home.HomeScreenContract.State.BookmarkMessage
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.navigation.map.BookmarksScreen
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.SettingsScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class HomeScreenPresenter(
  private val navigator: Navigator,
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<HomeScreenContract.State> {

  @Composable
  @Suppress("CyclomaticComplexMethod", "LongMethod")
  override fun present(): HomeScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()

    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }
    var servicesOrderToShow by rememberRetained {
      mutableStateOf<Unit?>(null)
    }
    var bookmarkMessage by rememberRetained {
      mutableStateOf<BookmarkMessage?>(value = null)
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

    val newsSources by produceRetainedState(emptyList<NewsSourceKind>().toImmutableList()) {
      peekApiService.collectAvailableSources().collect {
        if (it is Async.Content) {
          val existingIndex = contentTriggers.selectedNewsSourceIndex
          val newValue = it.content.map { source -> source.kind }.toImmutableList()

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
          kind = source,
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
      bookmarkMessage = bookmarkMessage,
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

          is Event.BookmarkToastResult -> {
            bookmarkMessage = null
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
                  val message = if (saved) "Added to bookmarks" else "Removed from bookmarks"

                  messageDispatcher.sendMessage(
                    MessageDispatcher.Message(
                      content = message,
                      type = MessageDispatcher.Message.MessageType.SUCCESS
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
}
