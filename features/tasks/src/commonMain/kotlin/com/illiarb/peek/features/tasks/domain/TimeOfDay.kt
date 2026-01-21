package com.illiarb.peek.features.tasks.domain

public enum class TimeOfDay {
  Anytime,
  Morning,
  Midday,
  Evening;

  public companion object {
    public fun fromString(value: String?): TimeOfDay {
      if (value == null) {
        return Anytime
      }
      return runCatching { valueOf(value) }.getOrElse { Anytime }
    }
  }
}
