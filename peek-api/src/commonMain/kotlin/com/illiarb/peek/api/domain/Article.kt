package com.illiarb.peek.api.domain

import com.illiarb.peek.core.types.Url
import kotlin.time.Duration
import kotlin.time.Instant

public data class Article(
  val url: Url,
  val title: String,
  val kind: NewsSourceKind,
  val date: Instant,
  val saved: Boolean,
)

public data class ArticlesOfKind(
  val kind: NewsSourceKind,
  val articles: List<Article>,
  val lastUpdated: Duration,
)
