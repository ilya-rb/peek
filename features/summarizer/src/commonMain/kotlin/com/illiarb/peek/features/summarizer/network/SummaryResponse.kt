package com.illiarb.peek.features.summarizer.network

import com.illiarb.peek.core.types.Url
import com.illiarb.peek.features.summarizer.domain.ArticleSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SummaryResponse(
  @SerialName("output") val output: List<Output>,
) {

  @Serializable
  data class Output(
    @SerialName("type") val type: String,
    @SerialName("content") val content: List<Message>? = null,
  )

  @Serializable
  data class Message(
    @SerialName("type") val type: String,
    @SerialName("text") val text: String,
  )

  fun asArticleSummary(url: Url): ArticleSummary {
    return ArticleSummary(
      url = url,
      content = output.asSequence()
        .filter { it.type == TYPE_MESSAGE }
        .mapNotNull { it.content }
        .flatten()
        .map { it.text }
        .joinToString("\n")
    )
  }

  companion object {
    const val TYPE_MESSAGE = "message"
  }
}
