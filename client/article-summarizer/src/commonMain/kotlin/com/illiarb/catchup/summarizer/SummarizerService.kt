package com.illiarb.catchup.summarizer

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import com.illiarb.catchup.summarizer.repository.SummarizerRepository
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