package com.illiarb.peek.features.tasks.domain

public enum class TimeOfDay {
  ANYTIME,
  MORNING,
  MIDDAY,
  EVENING;

  public companion object {
    public fun fromString(value: String?): TimeOfDay {
      if (value == null) {
        return ANYTIME
      }
      return runCatching { valueOf(value) }.getOrElse { ANYTIME }
    }
  }
}
