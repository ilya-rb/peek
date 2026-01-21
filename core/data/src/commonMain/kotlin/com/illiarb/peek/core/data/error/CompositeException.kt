package com.illiarb.peek.core.data.error

public class CompositeException(vararg suppressed: Throwable?) : Throwable(
  "Composite of ${suppressed.count { it != null }} exception(s)"
) {
  init {
    require(suppressed.isNotEmpty()) {
      "Cannot create CompositeException without any suppressed errors"
    }
    suppressed.forEach {
      if (it != null) {
        addSuppressed(it)
      }
    }
  }
}

