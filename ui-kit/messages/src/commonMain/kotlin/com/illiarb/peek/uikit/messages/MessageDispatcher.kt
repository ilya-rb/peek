package com.illiarb.peek.uikit.messages

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow

@Stable
public interface MessageDispatcher {

  public fun sendMessage(message: Message)
}

@Stable
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
