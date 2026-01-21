package com.illiarb.peek.core.data.error

public class CompositeException(vararg suppressed: Throwable?) : Throwable() {
  init {
    suppressed.forEach {
      if (it != null) {
        addSuppressed(it)
      }
    }
  }
}

