package com.illiarb.catchup.service.network.dto

import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.Url

import io.ktor.http.URLBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsSourcesDto(
  @SerialName("sources") val sources: List<NewsSourceDto>,
) {

  fun asNewsSourcesSet(baseUrl: String): Set<NewsSource> {
    return sources.map { source ->
      NewsSource(
        kind = NewsSource.Kind.fromKey(source.id),
        imageUrl = Url(source.imageUrl.fixUrl(baseUrl)),
      )
    }.toSet()
  }

  // TODO: Fix on BE
  private fun String.fixUrl(baseUrl: String): String {
    val currentUrl = io.ktor.http.Url(urlString = this)
    val apiBaseUrl = io.ktor.http.Url(baseUrl)

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
