package com.illiarb.peek.uikit.messages

import kotlinx.coroutines.flow.Flow

public interface MessageDispatcher {

  public fun sendMessage(message: Message)
}

public interface MessageProvider {

  public val messages: Flow<Message?>
}

public enum class MessageType {
  SUCCESS,
  ERROR,
}

public data class Message(
  val content: String,
  val type: MessageType,
)
