package com.illiarb.catchup.service.domain

import kotlinx.datetime.Instant

public data class Article(
  val id: String,
  val title: String,
  val link: Url,
  val tags: List<Tag>,
  val source: NewsSource.Kind,
  val date: Instant,
  val saved: Boolean,
)
