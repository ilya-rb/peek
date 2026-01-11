package com.illiarb.peek.features.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.OpenUrlScreen
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.illiarb.peek.features.reader.ReaderScreenContract.Event
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch

internal class ReaderScreenPresenter(
  private val navigator: Navigator,
  private val screen: ReaderScreen,
  private val peekApiService: PeekApiService,
) : Presenter<ReaderScreenContract.State> {

  @Composable
  @Suppress("CyclomaticComplexMethod")
  override fun present(): ReaderScreenContract.State {
    val article by produceRetainedState<Async<Article>>(initialValue = Async.Loading) {
      peekApiService.collectArticleByUrl(screen.url).collect {
        value = it
      }
    }
    var showTopBarPopup by rememberRetained { mutableStateOf(false) }
    var showSummary by rememberRetained { mutableStateOf(false) }

    var showRemoveBookmarkConfirmation by rememberRetained {
      mutableStateOf(false)
    }
    var removeBookmarkConfirmationShown by rememberRetained {
      mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    return ReaderScreenContract.State(
      article = article,
      showTopBarPopup = showTopBarPopup,
      showSummary = showSummary,
      showRemoveBookmarkConfirmation = showRemoveBookmarkConfirmation,
    ) { event ->
      if (event is Event.TopBarMenuAction) {
        showTopBarPopup = false
      }

      when (event) {
        is Event.NavigationIconClicked -> {
          navigator.pop()
        }

        is Event.TopBarMenuClicked -> {
          showTopBarPopup = true
        }

        is Event.TopBarMenuDismissed -> {
          showTopBarPopup = false
        }

        is Event.SummarizeResult -> {
          showSummary = false
        }

        is Event.ErrorRetryClicked -> Unit

        is Event.TopBarShare -> {
          val url = requireNotNull(article.contentOrNull()).url
          navigator.goTo(ShareScreen(url))
        }

        is Event.TopBarSummarize -> {
          showSummary = true
        }

        is Event.TopBarOpenInBrowser -> {
          val content = article
          require(content is Async.Content)

          navigator.goTo(OpenUrlScreen(content.content.url))
        }

        is Event.ScrolledToEnd -> {
          val content = article.contentOrNull()
          if (content != null && content.saved && !removeBookmarkConfirmationShown) {
            removeBookmarkConfirmationShown = true
            showRemoveBookmarkConfirmation = true
          }
        }

        is Event.RemoveBookmarkResult -> {
          showRemoveBookmarkConfirmation = false

          val content = article.contentOrNull()
          if (content != null && event.remove) {
            scope.launch {
              peekApiService.saveArticle(content.copy(saved = false))
            }
          }
        }
      }
    }
  }
}
