package com.illiarb.peek.features.summarizer.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SummaryRequest(
  @SerialName("model") val model: String,
  @SerialName("input") val input: List<Input>,
  @SerialName("tools") val tools: List<Tool>,
) {

  @Serializable
  data class Input(
    @SerialName("role") val role: String,
    @SerialName("content") val content: List<Message>,
  )

  @Serializable
  data class Message(
    @SerialName("type") val type: String,
    @SerialName("text") val text: String,
  )

  @Serializable
  data class Tool(
    @SerialName("type") val type: String
  )
}
