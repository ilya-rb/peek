package com.illiarb.catchup.core.logging

import io.github.aakira.napier.Napier

public object Logger {

  public fun i(tag: String, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.i(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun d(tag: String, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.d(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun w(tag: String, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.w(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun v(tag: String, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.v(tag = tag, throwable = throwable, message = messageLazy)
  }

  public fun e(tag: String, throwable: Throwable? = null, messageLazy: () -> String) {
    Napier.e(tag = tag, throwable = throwable, message = messageLazy)
  }
}
