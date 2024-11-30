package com.illiarb.catchup.service.network.dto

import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.service.domain.Url
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
  @SerialName("description") val description: String?,
  @SerialName("link") val link: String,
  @SerialName("source") val source: Source,
  @SerialName("tags") val tags: List<String>?,
) {

  fun asArticle(): Article {
    return Article(
      id = id,
      title = title,
      description = description?.trimIndent(),
      link = Url(link),
      source = NewsSource.Kind.fromKey(source.key),
      tags = tags.orEmpty().map(::Tag),
    )
  }
}

@Serializable
internal data class Source(
  val key: String,
  val kind: String,
)
