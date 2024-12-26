package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.catchup.core.arch.OpenUrlScreen
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.reader.ReaderScreen.Event
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.domain.Article
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import me.tatarka.inject.annotations.Inject

@Inject
public class ReaderScreenPresenterFactory(
  private val catchupService: CatchupService,
) : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is ReaderScreen -> ReaderScreenPresenter(
        articleId = screen.articleId,
        catchupService = catchupService,
        navigator = navigator,
      )

      else -> null
    }
  }
}

internal class ReaderScreenPresenter(
  private val articleId: String,
  private val catchupService: CatchupService,
  private val navigator: Navigator,
) : Presenter<ReaderScreen.State> {

  @Composable
  override fun present(): ReaderScreen.State {
    val article by produceRetainedState<Async<Article>>(initialValue = Async.Loading) {
      catchupService.collectArticleById(articleId).collect {
        value = it
      }
    }
    var topBarPopupShowing by rememberRetained { mutableStateOf(false) }

    return ReaderScreen.State(
      article = article,
      topBarPopupShowing = topBarPopupShowing,
    ) { event ->
      when (event) {
        is Event.NavigationIconClicked -> navigator.pop()
        is Event.SummarizeClicked -> Unit
        is Event.TopBarMenuClicked -> topBarPopupShowing = true
        is Event.TopBarMenuDismissed -> topBarPopupShowing = false
        is Event.ErrorRetryClicked -> Unit
        is Event.OpenInBrowserClicked -> {
          val content = article
          require(content is Async.Content)

          navigator.goTo(OpenUrlScreen(content.content.link.url))
        }
      }
    }
  }
}