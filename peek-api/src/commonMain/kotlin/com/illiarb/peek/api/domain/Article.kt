package com.illiarb.peek.api.domain

import com.illiarb.peek.core.types.Url
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

public data class Article(
  val url: Url,
  val title: String,
  val kind: NewsSourceKind,
  val date: Instant,
  val dateFormatted: String = dateFormatted(date),
  val saved: Boolean,
  val stale: Boolean = stale(date),
)

public data class ArticlesOfKind(
  val kind: NewsSourceKind,
  val articles: List<Article>,
  val lastUpdated: Instant,
)

private fun stale(date: Instant, thresholdDays: Duration = 7.days): Boolean {
  val now = Clock.System.now()
  val threshold = now - thresholdDays
  return date < threshold
}

private fun dateFormatted(date: Instant): String {
  return date.toLocalDateTime(TimeZone.UTC).format(dateFormat)
}

private val dateFormat: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
  monthName(MonthNames.ENGLISH_ABBREVIATED)
  char(' ')
  dayOfMonth()
  char(' ')
  year()
}
