package com.illiarb.peek.api.network.dto

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.core.types.Url
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ArticlesDto(
  @SerialName("articles")
  val articles: List<ArticleDto>,
)

@Serializable
internal data class ArticleDto(
  @SerialName("link") val url: String,
  @SerialName("title") val title: String,
  @SerialName("source") val source: Source,
  @SerialName("tags") val tags: List<String>?,
  @SerialName("date") val date: String,
) {

  fun asArticle(savedArticleUrls: List<Url>): Article {
    val url = Url(url)

    return Article(
      title = title,
      url = url,
      source = NewsSource.Kind.fromKey(source.key),
      tags = tags.orEmpty().map(::Tag),
      saved = url in savedArticleUrls,
      date = Instant.parse(date),
    )
  }
}

@Serializable
internal data class Source(
  @SerialName("key")
  val key: String,
  @SerialName("kind")
  val kind: String,
)
