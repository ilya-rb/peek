package com.illiarb.catchup.service.domain

import kotlin.time.Duration

public data class Article(
  val id: String,
  val title: String,
  val shortSummary: String?,
  val link: Url,
  val tags: List<Tag>,
  val source: NewsSource.Kind,
  val authorName: String?,
  val content: ArticleContent?,
)

public data class ArticleContent(
  val text: String,
  val estimatedReadingTime: Duration,
)
