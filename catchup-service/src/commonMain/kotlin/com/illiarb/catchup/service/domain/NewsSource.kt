package com.illiarb.catchup.service.domain

data class NewsSource(
  val kind: Kind,
  val imageUrl: Url,
) {

  enum class Kind(val key: String) {
    IrishTimes(key = "irishtimes"),
    HackerNews(key = "hackernews"),
    Dou(key = "dou"),
    Unknown(key = "unknown");

    companion object {

      fun fromKey(key: String): Kind {
        return when (key) {
          IrishTimes.key -> IrishTimes
          HackerNews.key -> HackerNews
          Dou.key -> Dou
          else -> Unknown
        }
      }
    }
  }
}
