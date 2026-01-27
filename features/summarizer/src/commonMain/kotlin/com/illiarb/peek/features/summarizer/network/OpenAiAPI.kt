package com.illiarb.peek.features.summarizer.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public object OpenAiAPIConfig {
  public const val MODEL: String = "gpt-4.1-mini"
}

@Serializable
internal data class CompletionRequest(
  @SerialName("model") val model: String,
  @SerialName("messages") val messages: List<Message>,
)

@Serializable
internal data class CompletionResponse(
  @SerialName("choices") val choices: List<Choice>,
  @SerialName("usage") val usage: Usage,
) {
  @Serializable
  internal data class Choice(
    @SerialName("message") val message: Message,
  )

  @Serializable
  internal data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("prompt_tokens_details") val promptTokensDetails: PromptTokensDetails,
  )

  @Serializable
  internal data class PromptTokensDetails(
    @SerialName("cached_tokens") val cachedTokens: Int,
  )
}

@Serializable
internal data class Message(
  @SerialName("role") val role: String,
  @SerialName("content") val content: String,
)

@Serializable
internal data class RequestError(
  @SerialName("message") val message: String,
)
