package com.illiarb.peek.api.domain

import com.illiarb.peek.core.types.Url
import kotlin.time.Instant

public data class Article(
  val url: Url,
  val title: String,
  val tags: List<Tag>,
  val kind: NewsSourceKind,
  val date: Instant,
  val saved: Boolean,
)
