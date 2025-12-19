package com.illiarb.peek.summarizer.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SummaryRequest(
  @SerialName("messages") val messages: List<Message>,
  @SerialName("model") val model: String,
  @SerialName("max_completion_tokens") val maxTokens: Int,
) {

  @Serializable
  data class Message(
    @SerialName("content") val content: String,
    @SerialName("role") val role: String,
  )
}