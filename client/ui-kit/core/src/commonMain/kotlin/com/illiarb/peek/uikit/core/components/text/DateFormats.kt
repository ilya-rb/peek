package com.illiarb.peek.uikit.core.components.text

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

public object DateFormats {

  public val default: DateTimeFormat<LocalDateTime> by lazy(LazyThreadSafetyMode.NONE) {
    LocalDateTime.Format {
      monthName(MonthNames.ENGLISH_ABBREVIATED)
      char(' ')
      dayOfMonth()
      char(' ')
      year()
    }
  }
}