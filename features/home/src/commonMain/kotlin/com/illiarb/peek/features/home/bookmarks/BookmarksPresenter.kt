package com.illiarb.peek.features.home.bookmarks

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.arch.message.MessageDispatcher.Message
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.bookmarks_action_removed
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class BookmarksPresenter(
  private val navigator: Navigator,
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<BookmarksScreenContract.State> {

  @Composable
  @Suppress("LongMethod")
  override fun present(): BookmarksScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()

    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }

    val articles by produceRetainedState<Async<ImmutableList<Article>>>(Async.Loading) {
      peekApiService.collectSavedArticles()
        .mapContent { it.toImmutableList() }
        .collect { value = it }
    }

    val searchState = rememberTextFieldState()

    return BookmarksScreenContract.State(
      articles = articles,
      search = searchState,
      articleSummaryToShow = articleSummaryToShow,
      articlesEventSink = { event ->
        when (event) {
          is ArticlesUi.ArticleBookmarkClicked -> {
            coroutineScope.launch {
              val article = event.item.copy(saved = false)

              peekApiService.saveArticle(article).onSuccess {
                messageDispatcher.sendMessage(
                  Message(
                    content = getString(Res.string.bookmarks_action_removed),
                    type = Message.MessageType.SUCCESS,
                  )
                )
              }
            }
          }

          is ArticlesUi.ArticleClicked -> {
            navigator.goTo(ReaderScreen(event.item.url))
          }

          is ArticlesUi.ArticleShareClicked -> {
            navigator.goTo(ShareScreen(Url(event.item.url.url)))
          }

          is ArticlesUi.ArticleSummarizeClicked -> {
            articleSummaryToShow = event.item
          }

          is ArticlesUi.ArticlesRefreshClicked -> Unit
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
