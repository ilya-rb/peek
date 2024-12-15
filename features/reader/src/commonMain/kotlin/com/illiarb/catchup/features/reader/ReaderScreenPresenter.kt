package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.illiarb.catchup.core.arch.OpenUrlScreen
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.domain.Article
import com.slack.circuit.retained.produceRetainedState
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
) : Presenter<ReaderScreenContract.State> {

  @Composable
  override fun present(): ReaderScreenContract.State {
    val article by produceRetainedState<Async<Article>>(initialValue = Async.Loading) {
      catchupService.collectArticleById(articleId).collect {
        value = it
      }
    }

    return ReaderScreenContract.State(article) { event ->
      when (event) {
        is ReaderScreenContract.Event.NavigationIconClicked -> {
          navigator.pop()
        }

        is ReaderScreenContract.Event.LinkClicked -> {
          navigator.goTo(OpenUrlScreen(event.url.url))
        }

        is ReaderScreenContract.Event.ErrorRetryClicked -> {

        }
      }
    }
  }
}