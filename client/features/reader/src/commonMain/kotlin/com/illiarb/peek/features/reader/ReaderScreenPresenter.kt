package com.illiarb.peek.features.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.OpenUrlScreen
import com.illiarb.peek.core.arch.ShareScreen
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.reader.ReaderScreen.Event
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
public class ReaderScreenPresenter(
  @Assisted private val navigator: Navigator,
  @Assisted private val screen: ReaderScreen,
  private val peekApiService: PeekApiService,
) : Presenter<ReaderScreen.State> {

  @Composable
  override fun present(): ReaderScreen.State {
    val article by produceRetainedState<Async<Article>>(initialValue = Async.Loading) {
      peekApiService.collectArticleByUrl(screen.url).collect {
        value = it
      }
    }
    var topBarPopupShowing by rememberRetained { mutableStateOf(false) }
    var summaryShowing by rememberRetained { mutableStateOf(false) }

    return ReaderScreen.State(
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

        is Event.SummarizeCloseClicked -> {
          summaryShowing = false
        }

        is Event.ErrorRetryClicked -> Unit

        is Event.TopBarShare -> {
          val url = requireNotNull(article.contentOrNull()).url.url
          navigator.goTo(ShareScreen(url))
        }

        is Event.TopBarSummarize -> {
          summaryShowing = true
        }

        is Event.TopBarOpenInBrowser -> {
          val content = article
          require(content is Async.Content)

          navigator.goTo(OpenUrlScreen(content.content.url.url))
        }
      }
    }
  }

  @AssistedFactory
  @CircuitInject(ReaderScreen::class, UiScope::class)
  public fun interface Factory {
    public fun create(navigator: Navigator, screen: ReaderScreen): ReaderScreenPresenter
  }
}