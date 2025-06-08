package com.illiarb.catchup.service.domain

public data class NewsSource(
  val kind: Kind,
  val imageUrl: Url,
) {

  public enum class Kind(public val key: String) {
    IrishTimes(key = "irishtimes"),
    HackerNews(key = "hackernews"),
    Dou(key = "dou");

    public companion object {

      public fun fromKey(key: String): Kind {
        return when (key) {
          IrishTimes.key -> IrishTimes
          HackerNews.key -> HackerNews
          Dou.key -> Dou
          else -> throw IllegalArgumentException("Unknown key: $key")
        }
      }
    }
  }
}
