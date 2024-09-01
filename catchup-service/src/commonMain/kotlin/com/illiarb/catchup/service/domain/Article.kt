package com.illiarb.catchup.service.domain

data class Article(
  val id: String,
  val title: String,
  val description: String?,
  val link: String,
  val tags: List<String>,
  val sourceId: NewsSourceId,
)
