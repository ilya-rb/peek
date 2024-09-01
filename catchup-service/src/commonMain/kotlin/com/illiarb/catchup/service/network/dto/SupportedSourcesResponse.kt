package com.illiarb.catchup.service.network.dto

import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.NewsSourceId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsSourcesResponse(
  @SerialName("sources") val sources: List<NewsSourceDto>,
) {

  fun asNewsSourcesSet(): Set<NewsSource> {
    return sources.map { source ->
      NewsSource(
        id = NewsSourceId.fromKey(source.id),
        imageUrl = source.imageUrl,
      )
    }.toSet()
  }
}

@Serializable
internal data class NewsSourceDto(
  @SerialName("id") val id: String,
  @SerialName("imageUrl") val imageUrl: String,
)
