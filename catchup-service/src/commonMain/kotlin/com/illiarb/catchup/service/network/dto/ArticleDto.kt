package com.illiarb.catchup.service.network.dto

import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSourceId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ArticleDto(
  @SerialName("id") val id: String,
  @SerialName("title") val title: String,
  @SerialName("description") val description: String?,
  @SerialName("link") val link: String,
  @SerialName("source") val sourceId: String,
  @SerialName("tags") val tags: List<String>?,
) {

  fun asArticle(): Article {
    return Article(
      id = id,
      title = title.normalize(),
      description = description?.trimIndent(),
      link = link,
      sourceId = NewsSourceId.valueOf(sourceId),
      tags = tags.orEmpty(),
    )
  }

  private fun String.normalize(): String {
    return replace("\n", "").replace("\t", "")
  }
}
