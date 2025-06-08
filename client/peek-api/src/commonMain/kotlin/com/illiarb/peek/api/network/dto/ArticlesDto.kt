package com.illiarb.peek.api.network.dto

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.api.domain.Url
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
  @SerialName("id") val id: String,
  @SerialName("title") val title: String,
  @SerialName("link") val link: String,
  @SerialName("source") val source: Source,
  @SerialName("tags") val tags: List<String>?,
  @SerialName("date") val date: String,
) {

  fun asArticle(savedArticleIds: List<String>): Article {
    return Article(
      id = id,
      title = title,
      link = Url(link),
      source = NewsSource.Kind.fromKey(source.key),
      tags = tags.orEmpty().map(::Tag),
      saved = id in savedArticleIds,
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
