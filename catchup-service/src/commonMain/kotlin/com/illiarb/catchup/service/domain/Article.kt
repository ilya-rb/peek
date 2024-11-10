package com.illiarb.catchup.service.domain

data class Article(
  val id: String,
  val title: String,
  val description: String?,
  val link: Url,
  val tags: List<Tag>,
  val source: NewsSourceKind,
)
