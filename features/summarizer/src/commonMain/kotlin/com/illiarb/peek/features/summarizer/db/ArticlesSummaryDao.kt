package com.illiarb.peek.features.summarizer.db

import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.features.summarizer.Database
import com.illiarb.peek.features.summarizer.Summaries
import com.illiarb.peek.features.summarizer.di.InternalApi
import com.illiarb.peek.features.summarizer.domain.ArticleSummary
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class ArticlesSummaryDao(
  private val appDispatchers: AppDispatchers,
  @InternalApi private val db: Database,
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
        )
        Unit
      }
    }
  }

  private fun Summaries.toDomain(): ArticleSummary {
    return ArticleSummary(url, summary)
  }
}
