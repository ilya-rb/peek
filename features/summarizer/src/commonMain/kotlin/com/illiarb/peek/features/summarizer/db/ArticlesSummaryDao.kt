package com.illiarb.peek.features.summarizer.db

import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.features.summarizer.Database
import com.illiarb.peek.features.summarizer.Summaries
import com.illiarb.peek.features.summarizer.di.InternalApi
import com.illiarb.peek.features.summarizer.domain.ArticleSummary
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
@InternalApi
internal class ArticlesSummaryDao(
  private val appDispatchers: AppDispatchers,
  @InternalApi private val db: Database,
) {

  suspend fun summaryByUrl(url: Url): Result<ArticleSummary?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.summaryByUrl(url.url).executeAsOneOrNull()?.toDomain()
      }
    }
  }

  suspend fun saveSummary(summary: ArticleSummary): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.saveSummary(
          url = summary.url.url,
          summary = summary.content,
        )
        Unit
      }
    }
  }

  private fun Summaries.toDomain(): ArticleSummary {
    return ArticleSummary(Url(url), summary)
  }
}
