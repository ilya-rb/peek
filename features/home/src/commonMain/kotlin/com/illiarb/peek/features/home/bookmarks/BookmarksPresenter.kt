package com.illiarb.peek.features.home.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class BookmarksPresenter(
  private val navigator: Navigator,
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<BookmarksScreenContract.State> {

  @Composable
  override fun present(): BookmarksScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()

    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }

    var contentTriggers by rememberRetained {
      mutableStateOf(
        BookmarksScreenContract.ContentTriggers(
          articleBookmarked = false,
          contentForceRefresh = false,
        )
      )
    }

    val articles by produceRetainedState<Async<ImmutableList<Article>>>(
      initialValue = Async.Loading,
      key1 = contentTriggers,
    ) {
      peekApiService.collectSavedArticles()
        .mapContent { it.toImmutableList() }
        .collect { value = it }
    }

    return BookmarksScreenContract.State(
      articles = articles,
      articleSummaryToShow = articleSummaryToShow,
      articlesEventSink = { event ->
        when (event) {
          is ArticlesUiEvent.ArticleBookmarkClicked -> {
            coroutineScope.launch {
              val article = event.item.copy(saved = false)

              peekApiService.saveArticle(article).onSuccess {
                contentTriggers = contentTriggers.copy(
                  articleBookmarked = !contentTriggers.articleBookmarked
                )
                messageDispatcher.sendMessage(
                  MessageDispatcher.Message(
                    content = "Removed from bookmarks",
                    type = MessageDispatcher.Message.MessageType.SUCCESS,
                  )
                )
              }
            }
          }

          is ArticlesUiEvent.ArticleClicked -> {
            navigator.goTo(ReaderScreen(event.item.url))
          }

          is ArticlesUiEvent.ArticleShareClicked -> {
            navigator.goTo(ShareScreen(Url(event.item.url.url)))
          }

          is ArticlesUiEvent.ArticleSummarizeClicked -> {
            articleSummaryToShow = event.item
          }

          is ArticlesUiEvent.ArticlesRefreshClicked -> {
            contentTriggers = contentTriggers.copy(
              contentForceRefresh = !contentTriggers.contentForceRefresh
            )
          }
        }
      },
      eventSink = { event ->
        when (event) {
          BookmarksScreenContract.Event.ErrorRetryClicked -> Unit
          BookmarksScreenContract.Event.NavigationButtonClicked -> navigator.pop()
          BookmarksScreenContract.Event.SummaryCloseClicked -> articleSummaryToShow = null
        }
      },
    )
  }
}