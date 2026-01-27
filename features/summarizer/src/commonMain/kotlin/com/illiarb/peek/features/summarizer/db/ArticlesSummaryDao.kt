package com.illiarb.peek.features.summarizer.db

import com.illiarb.peek.core.coroutines.CoroutineDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.types.Currency
import com.illiarb.peek.core.types.Money
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
  private val coroutineDispatchers: CoroutineDispatchers,
  @InternalApi private val db: Database,
) {

  suspend fun summaryByUrl(url: Url): Result<ArticleSummary?> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.summaryByUrl(url.url).executeAsOneOrNull()?.toDomain()
      }
    }
  }

  suspend fun saveSummary(summary: ArticleSummary): Result<Unit> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.saveSummary(
          url = summary.url.url,
          summary = summary.content,
          model = summary.model,
          price = summary.price.amount,
          currency = summary.price.currency.code,
        )
        Unit
      }
    }
  }

  private fun Summaries.toDomain(): ArticleSummary {
    return ArticleSummary(
      url = Url(url),
      content = summary,
      model = model,
      price = Money(price, Currency.ofCode(this.currency))
    )
  }
}
