package com.illiarb.peek.features.tasks.domain

/**
 * Represents the time of day when a task should be completed.
 * If null, the task is considered "Anytime".
 */
public enum class TimeOfDay {
  MORNING,
  MIDDAY,
  EVENING;

  public companion object {
    public fun fromString(value: String?): TimeOfDay? {
      return when (value) {
        "MORNING" -> MORNING
        "MIDDAY" -> MIDDAY
        "EVENING" -> EVENING
        else -> null
      }
    }
  }
}
