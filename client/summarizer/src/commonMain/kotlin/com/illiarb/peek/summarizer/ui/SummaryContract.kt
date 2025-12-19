package com.illiarb.peek.summarizer.ui

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.summarizer.domain.ArticleSummary
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data class SummaryScreen(
  val url: Url,
  val context: Context,
) : Screen, CommonParcelable {

  public enum class Context {
    HOME,
    READER,
  }

  public sealed interface Result : PopResult, CommonParcelable {

    @CommonParcelize
    public data object Close : Result

    @CommonParcelize
    public data class OpenInReader(val url: Url) : Result
  }

  public data class State(
    val articleWithSummary: Async<ArticleWithSummary>,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  public data class ArticleWithSummary(
    val article: Article,
    val summary: ArticleSummary,
  )

  public sealed interface Event : CircuitUiEvent {
    public data object NavigationIconClick : Event
    public data class OpenInReaderClick(val article: Article) : Event
  }
}