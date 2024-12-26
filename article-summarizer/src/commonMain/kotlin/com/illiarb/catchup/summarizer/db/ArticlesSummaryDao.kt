package com.illiarb.catchup.summarizer.db

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.coroutines.suspendRunCatching
import com.illiarb.catchup.summarizer.Database
import com.illiarb.catchup.summarizer.Summaries
import com.illiarb.catchup.summarizer.di.SummarizerApi
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@Inject
public class ArticlesSummaryDao(
  private val appDispatchers: AppDispatchers,
  @SummarizerApi private val db: Database,
) {

  public suspend fun summaryByUrl(url: String): Result<ArticleSummary?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.summaryByUrl(url).executeAsOneOrNull()?.toDomain()
      }
    }
  }

  public suspend fun saveSummary(summary: ArticleSummary): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.saveSummary(
          url = summary.url,
          summary = summary.content,
          createdAt = Clock.System.now(),
        )
      }
    }
  }

  private fun Summaries.toDomain(): ArticleSummary {
    return ArticleSummary(url, summary)
  }
}