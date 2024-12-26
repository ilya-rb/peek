package com.illiarb.catchup.summarizer.network

import com.illiarb.catchup.summarizer.domain.ArticleSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SummaryResponse(
  @SerialName("id") val id: String,
  @SerialName("object") val type: String,
  @SerialName("created") val created: Long,
  @SerialName("model") val model: String,
  @SerialName("usage") val usage: Usage,
  @SerialName("choices") val choices: List<Choice>,
) {

  @Serializable
  data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
  )

  @Serializable
  data class Choice(
    @SerialName("finish_reason") val finishReason: String,
    @SerialName("index") val index: Int,
    @SerialName("message") val message: Message,
  )

  @Serializable
  data class Message(
    @SerialName("role") val role: String = ApiConfig.ROLE,
    @SerialName("content") val content: String,
  )

  fun asArticleSummary(url: String): ArticleSummary {
    return ArticleSummary(
      url = url,
      content = choices.joinToString(separator = ",") { it.message.content }
    )
  }
}