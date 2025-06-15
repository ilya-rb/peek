package com.illiarb.peek.api.domain

import com.illiarb.peek.core.types.Url
import kotlinx.datetime.Instant

public data class Article(
  val url: Url,
  val title: String,
  val tags: List<Tag>,
  val source: NewsSource.Kind,
  val date: Instant,
  val saved: Boolean,
)
