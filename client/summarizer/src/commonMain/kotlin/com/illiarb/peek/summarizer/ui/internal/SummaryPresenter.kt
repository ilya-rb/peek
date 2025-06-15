package com.illiarb.peek.summarizer.ui.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.flatMapLatestContent
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.summarizer.SummarizerService
import com.illiarb.peek.summarizer.ui.SummaryScreen
import com.illiarb.peek.summarizer.ui.SummaryScreen.ArticleWithSummary
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import me.tatarka.inject.annotations.Inject

@Inject
public class SummaryPresenterFactory(
  private val peekApiService: PeekApiService,
  private val summarizerService: SummarizerService,
) : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is SummaryScreen -> SummaryPresenter(
        url = screen.url,
        peekApiService = peekApiService,
        summarizerService = summarizerService,
        navigator = navigator,
      )

      else -> null
    }
  }
}

internal class SummaryPresenter(
  private val url: Url,
  private val peekApiService: PeekApiService,
  private val summarizerService: SummarizerService,
  private val navigator: Navigator,
) : Presenter<SummaryScreen.State> {

  @Composable
  override fun present(): SummaryScreen.State {
    val articleWithSummary by produceRetainedState<Async<ArticleWithSummary>>(Async.Loading) {
      peekApiService.collectArticleByUrl(url)
        .flatMapLatestContent { article ->
          summarizerService.summarizeArticle(article.url.url).mapContent { summary ->
            ArticleWithSummary(
              article = article,
              summary = summary,
            )
          }
        }
        .collect {
          value = it
        }
    }

    return SummaryScreen.State(
      articleWithSummary = articleWithSummary,
      eventSink = { event ->
        when (event) {
          is SummaryScreen.Event.NavigationIconClick -> {
            navigator.pop(SummaryScreen.Result.Close)
          }

          is SummaryScreen.Event.OpenInReaderClick -> {
            navigator.pop(SummaryScreen.Result.OpenInReader(event.article.url))
          }
        }
      }
    )
  }
}