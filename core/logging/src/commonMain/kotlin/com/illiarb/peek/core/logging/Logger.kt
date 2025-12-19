package com.illiarb.peek.core.logging

import io.github.aakira.napier.Napier

public object Logger {

  private const val DEFAULT_TAG: String = "CoreLogger"

  public fun i(tag: String = DEFAULT_TAG, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.i(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun d(tag: String = DEFAULT_TAG, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.d(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun w(tag: String = DEFAULT_TAG, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.w(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun v(tag: String = DEFAULT_TAG, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.v(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun e(
    tag: String = DEFAULT_TAG,
    throwable: Throwable? = null,
    messageLazy: (() -> String)? = null
  ) {
    val messageLazy = messageLazy ?: { "" }
    Napier.e(tag = tag, throwable = throwable, message = messageLazy)
  }
}
