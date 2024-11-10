package com.illiarb.catchup.service.domain

data class NewsSource(
  val kind: NewsSourceKind,
  val imageUrl: Url,
)

enum class NewsSourceKind(val key: String) {
  IrishTimes(key = "irishtimes"),
  HackerNews(key = "hackernews"),
  Dou(key = "dou"),
  Unknown(key = "unknown");

  companion object {

    fun fromKey(key: String): NewsSourceKind {
      return when (key) {
        IrishTimes.key -> IrishTimes
        HackerNews.key -> HackerNews
        Dou.key -> Dou
        else -> Unknown
      }
    }
  }
}
