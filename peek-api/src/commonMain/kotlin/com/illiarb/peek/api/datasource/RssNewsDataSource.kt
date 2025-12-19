package com.illiarb.peek.api.datasource

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.logging.Logger
import com.illiarb.peek.core.types.Url
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssItem
import kotlinx.datetime.format.DateTimeComponents
import kotlin.time.Clock

internal class RssNewsDataSource(
  private val rssParser: RssParser,
  private val feedUrl: Url,
  override val kind: NewsSourceKind,
) : NewsDataSource {

  override suspend fun getArticles(): List<Article> {
    val channel = rssParser.getRssChannel(feedUrl.url)

    return channel.items.mapNotNull { item ->
      item.asArticle()
        .onFailure { error ->
          Logger.e(throwable = error) { "Failed to parse article" }
        }
        .getOrNull()
    }
  }

  private fun RssItem.asArticle(): Result<Article> {
    return runCatching {
      val title = requireNotNull(this.title) {
        "Title for article is missing $this"
      }
      val url = requireNotNull(this.link) {
        "Link is missing from article ${this.title}"
      }

      val date = pubDate
        ?.let { DateTimeComponents.Formats.RFC_1123.parse(it) }
        ?.toInstantUsingOffset()
        ?: Clock.System.now()

      Article(
        url = Url(url),
        title = title,
        tags = emptyList(),
        kind = kind,
        date = date,
        saved = false,
      )
    }
  }
}