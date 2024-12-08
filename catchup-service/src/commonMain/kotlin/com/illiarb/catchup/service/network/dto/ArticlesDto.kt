package com.illiarb.catchup.service.network.dto

import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.ArticleContent
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.service.domain.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

@Serializable
internal class ArticlesDto(
  @SerialName("articles")
  val articles: List<ArticleDto>,
)

@Serializable
internal data class ArticleDto(
  @SerialName("id") val id: String,
  @SerialName("title") val title: String,
  @SerialName("short_summary") val shortSummary: String?,
  @SerialName("link") val link: String,
  @SerialName("source") val source: Source,
  @SerialName("tags") val tags: List<String>?,
  @SerialName("author_name") val authorName: String?,
  @SerialName("content") val content: ArticleContentDto?,
) {

  fun asArticle(): Article {
    return Article(
      id = id,
      title = title,
      shortSummary = shortSummary?.trimIndent(),
      link = Url(link),
      source = NewsSource.Kind.fromKey(source.key),
      tags = tags.orEmpty().map(::Tag),
      authorName = authorName,
      content = content?.let { content ->
        ArticleContent(
          text = content.text,
          estimatedReadingTime = content.estimatedReadingTime.seconds,
        )
      }
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

@Serializable
internal data class ArticleContentDto(
  @SerialName("text")
  val text: String,
  @SerialName("estimated_reading_time_seconds")
  val estimatedReadingTime: Long,
)
