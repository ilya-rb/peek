package com.illiarb.peek.features.summarizer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.flatMapLatestContent
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.summarizer.SummarizerService
import com.illiarb.peek.features.summarizer.ui.SummaryScreenContract.State.ArticleWithSummary
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter

internal class SummaryScreenPresenter(
  private val navigator: Navigator,
  private val screen: SummaryScreen,
  private val peekApiService: PeekApiService,
  private val summarizerService: SummarizerService,
) : Presenter<SummaryScreenContract.State> {

  @Composable
  override fun present(): SummaryScreenContract.State {
    val articleWithSummary by produceRetainedState<Async<ArticleWithSummary>>(Async.Loading) {
      peekApiService.collectArticleByUrl(screen.url)
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

    return SummaryScreenContract.State(
      articleWithSummary = articleWithSummary,
      eventSink = { event ->
        when (event) {
          is SummaryScreenContract.Event.NavigationIconClick -> {
            navigator.pop(SummaryScreen.Result.Close)
          }

          is SummaryScreenContract.Event.OpenInReaderClick -> {
            navigator.pop(SummaryScreen.Result.OpenInReader(event.article.url))
          }
        }
      }
    )
  }
}