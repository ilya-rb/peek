package com.illiarb.peek.api.domain

import com.illiarb.peek.core.types.Url
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

public data class Article(
  val url: Url,
  val title: String,
  val kind: NewsSourceKind,
  val date: Instant,
  val saved: Boolean,
) {
  public fun stale(thresholdDays: Duration = 7.days): Boolean {
    val now = Clock.System.now()
    val threshold = now - thresholdDays
    return this.date < threshold
  }
}

public data class ArticlesOfKind(
  val kind: NewsSourceKind,
  val articles: List<Article>,
  val lastUpdated: Instant,
)
