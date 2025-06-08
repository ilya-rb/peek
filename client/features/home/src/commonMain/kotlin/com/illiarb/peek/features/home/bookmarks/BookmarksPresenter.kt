package com.illiarb.peek.features.home.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.illiarb.peek.core.arch.ShareScreen
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.illiarb.peek.features.reader.ReaderScreen
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
public class BookmarksScreenPresenterFactory(
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is BookmarksScreen -> BookmarksPresenter(navigator, peekApiService, messageDispatcher)
      else -> null
    }
  }
}

internal class BookmarksPresenter(
  private val navigator: Navigator,
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<BookmarksScreen.State> {

  @Composable
  override fun present(): BookmarksScreen.State {
    val coroutineScope = rememberStableCoroutineScope()

    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }

    var contentTriggers by rememberRetained {
      mutableStateOf(
        ContentTriggers(
          articleBookmarked = false,
          contentForceRefresh = false,
        )
      )
    }

    val articles by produceRetainedState<Async<SnapshotStateList<Article>>>(
      initialValue = Async.Loading,
      key1 = contentTriggers,
    ) {
      peekApiService.collectSavedArticles()
        .mapContent { it.toMutableStateList() }
        .collect { value = it }
    }

    return BookmarksScreen.State(
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
            navigator.goTo(ReaderScreen(event.item.id))
          }

          is ArticlesUiEvent.ArticleShareClicked -> {
            navigator.goTo(ShareScreen(event.item.link.url))
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
          BookmarksScreen.Event.ErrorRetryClicked -> Unit
          BookmarksScreen.Event.NavigationButtonClicked -> navigator.pop()
          BookmarksScreen.Event.SummaryCloseClicked -> articleSummaryToShow = null
        }
      },
    )
  }
}

private data class ContentTriggers(
  val articleBookmarked: Boolean,
  val contentForceRefresh: Boolean,
)