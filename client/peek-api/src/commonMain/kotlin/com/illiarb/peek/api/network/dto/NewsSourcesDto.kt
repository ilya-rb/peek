package com.illiarb.peek.api.network.dto

import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.types.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsSourcesDto(
  @SerialName("sources") val sources: List<NewsSourceDto>,
)

@Serializable
internal data class NewsSourceDto(
  @SerialName("id") val id: String,
  @SerialName("icon") val icon: String,
  @SerialName("name") val name: String,
)

internal fun List<NewsSourceDto>.toDomain(): Set<NewsSource> {
  return map { source ->
    NewsSource(
      kind = NewsSource.Kind.fromKey(source.id),
      icon = Url(source.icon),
      name = source.name,
    )
  }.toSet()
}
