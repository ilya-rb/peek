package com.illiarb.peek.api.domain

import kotlinx.serialization.Serializable

@Serializable
public data class NewsSource(
  val kind: NewsSourceKind,
  val order: Int,
)

public enum class NewsSourceKind {
  HackerNews,
  Dou,
  Ft
}
