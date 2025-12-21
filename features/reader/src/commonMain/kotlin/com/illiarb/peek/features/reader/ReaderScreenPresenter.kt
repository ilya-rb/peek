package com.illiarb.peek.features.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

internal class ReaderScreenPresenter(
  private val navigator: Navigator,
  private val screen: ReaderScreen,
  private val peekApiService: PeekApiService,
) : Presenter<ReaderScreenContract.State> {

  @Composable
  override fun present(): ReaderScreenContract.State {
    val article by produceRetainedState<Async<Article>>(initialValue = Async.Loading) {
      peekApiService.collectArticleByUrl(screen.url).collect {
        value = it
      }
    }
    var topBarPopupShowing by rememberRetained { mutableStateOf(false) }
    var summaryShowing by rememberRetained { mutableStateOf(false) }

    return ReaderScreenContract.State(
      article = article,
      topBarPopupShowing = topBarPopupShowing,
      summaryShowing = summaryShowing,
    ) { event ->
      if (event is Event.TopBarMenuAction) {
        topBarPopupShowing = false
      }

      when (event) {
        is Event.NavigationIconClicked -> {
          navigator.pop()
        }

        is Event.TopBarMenuClicked -> {
          topBarPopupShowing = true
        }

        is Event.TopBarMenuDismissed -> {
          topBarPopupShowing = false
        }

        is Event.SummarizeResult -> {
          summaryShowing = false
        }

        is Event.ErrorRetryClicked -> Unit

        is Event.TopBarShare -> {
          val url = requireNotNull(article.contentOrNull()).url
          navigator.goTo(ShareScreen(url))
        }

        is Event.TopBarSummarize -> {
          summaryShowing = true
        }

        is Event.TopBarOpenInBrowser -> {
          val content = article
          require(content is Async.Content)

          navigator.goTo(OpenUrlScreen(content.content.url))
        }
      }
    }
  }
}
