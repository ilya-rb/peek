package com.illiarb.peek.uikit.messages

import com.illiarb.peek.core.arch.di.UiScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

@Inject
@SingleIn(UiScope::class)
@ContributesBinding(scope = UiScope::class, binding = binding<MessageDispatcher>())
@ContributesBinding(scope = UiScope::class, binding = binding<MessageProvider>())
public class MessageMediator : MessageDispatcher, MessageProvider {

  private val _messages = MutableSharedFlow<Message>(extraBufferCapacity = 1)

  override val messages: Flow<Message> = _messages

  override fun sendMessage(message: Message) {
    _messages.tryEmit(message)
  }
}
