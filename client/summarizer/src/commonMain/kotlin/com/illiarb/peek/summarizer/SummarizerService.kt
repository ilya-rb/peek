package com.illiarb.peek.summarizer

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.summarizer.domain.ArticleSummary
import com.illiarb.peek.summarizer.repository.SummarizerRepository
import kotlinx.coroutines.flow.Flow

public interface SummarizerService {

  public fun summarizeArticle(url: String): Flow<Async<ArticleSummary>>
}

internal class DefaultSummarizerService(
  private val repository: SummarizerRepository,
) : SummarizerService {

  override fun summarizeArticle(url: String): Flow<Async<ArticleSummary>> {
    return repository.articleSummary(url)
  }
}