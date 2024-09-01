package com.illiarb.catchup.service.domain

data class NewsSource(
  val id: NewsSourceId,
  val imageUrl: String,
)

enum class NewsSourceId(val key: String) {
  IrishTimes(key = "irishtimes"),
  HackerNews(key = "hackernews"),
  Dou(key = "dou"),
  Unknown(key = "unknown");

  companion object {

    fun fromKey(key: String): NewsSourceId {
      return when (key) {
        IrishTimes.key -> IrishTimes
        HackerNews.key -> HackerNews
        Dou.key -> Dou
        else -> Unknown
      }
    }
  }
}
