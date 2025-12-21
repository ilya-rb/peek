package com.illiarb.peek.core.data.ext

public fun <T> Collection<T>.replace(new: T, finder: (T) -> Boolean): Collection<T> {
  return map { item ->
    if (finder(item)) {
      new
    } else {
      item
    }
  }
}
