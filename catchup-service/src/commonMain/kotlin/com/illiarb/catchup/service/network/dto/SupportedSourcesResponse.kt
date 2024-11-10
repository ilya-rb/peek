package com.illiarb.catchup.service.network.dto

import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.NewsSourceKind
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsSourcesResponse(
  @SerialName("sources") val sources: List<NewsSourceDto>,
) {

  fun asNewsSourcesSet(baseUrl: String): Set<NewsSource> {
    return sources.map { source ->
      NewsSource(
        kind = NewsSourceKind.fromKey(source.id),
        imageUrl = com.illiarb.catchup.service.domain.Url(source.imageUrl.fixUrl(baseUrl)),
      )
    }.toSet()
  }

  // TODO: Fix on BE
  private fun String.fixUrl(baseUrl: String): String {
    val currentUrl = Url(urlString = this)
    val apiBaseUrl = Url(baseUrl)

    return URLBuilder(currentUrl)
      .apply {
        host = apiBaseUrl.host
        port = apiBaseUrl.port
      }
      .buildString()
  }
}

@Serializable
internal data class NewsSourceDto(
  @SerialName("id") val id: String,
  @SerialName("imageUrl") val imageUrl: String,
)
