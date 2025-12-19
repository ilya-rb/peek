package com.illiarb.peek.summarizer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.flatMapLatestContent
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.summarizer.SummarizerService
import com.illiarb.peek.summarizer.ui.SummaryScreen.ArticleWithSummary
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
public class SummaryPresenter(
  @Assisted private val navigator: Navigator,
  @Assisted private val screen: SummaryScreen,
  private val peekApiService: PeekApiService,
  private val summarizerService: SummarizerService,
) : Presenter<SummaryScreen.State> {

  @Composable
  override fun present(): SummaryScreen.State {
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

  @AssistedFactory
  @CircuitInject(SummaryScreen::class, UiScope::class)
  public fun interface Factory {
    public fun create(navigator: Navigator, screen: SummaryScreen): SummaryPresenter
  }
}