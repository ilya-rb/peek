package com.illiarb.catchup.summarizer.ui.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.flatMapLatestContent
import com.illiarb.catchup.core.data.mapContent
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.summarizer.SummarizerService
import com.illiarb.catchup.summarizer.ui.SummaryScreen
import com.illiarb.catchup.summarizer.ui.SummaryScreen.ArticleWithSummary
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import me.tatarka.inject.annotations.Inject

@Inject
public class SummaryPresenterFactory(
  private val catchupService: CatchupService,
  private val summarizerService: SummarizerService,
) : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is SummaryScreen -> SummaryPresenter(
        articleId = screen.articleId,
        catchupService = catchupService,
        summarizerService = summarizerService,
        navigator = navigator,
      )

      else -> null
    }
  }
}

internal class SummaryPresenter(
  private val articleId: String,
  private val catchupService: CatchupService,
  private val summarizerService: SummarizerService,
  private val navigator: Navigator,
) : Presenter<SummaryScreen.State> {

  @Composable
  override fun present(): SummaryScreen.State {
    val articleWithSummary by produceRetainedState<Async<ArticleWithSummary>>(Async.Loading) {
      catchupService.collectArticleById(articleId)
        .flatMapLatestContent { article ->
          summarizerService.summarizeArticle(article.link.url).mapContent { summary ->
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
            navigator.pop(SummaryScreen.Result.OpenInReader(event.article.id))
          }
        }
      }
    )
  }
}