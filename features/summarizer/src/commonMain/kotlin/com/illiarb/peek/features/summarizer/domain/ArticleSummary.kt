package com.illiarb.peek.features.summarizer.domain

import com.illiarb.peek.core.types.Url

public data class ArticleSummary(
  val url: Url,
  val content: String,
)
