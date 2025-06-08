package com.illiarb.peek.core.arch.message

import kotlinx.coroutines.flow.Flow

public interface MessageDispatcher {

  public fun sendMessage(message: Message)

  public data class Message(
    val content: String,
    val type: MessageType
  ) {
    public enum class MessageType {
      SUCCESS,
      ERROR,
    }
  }
}

public interface MessageProvider {

  public val messages: Flow<MessageDispatcher.Message?>
}