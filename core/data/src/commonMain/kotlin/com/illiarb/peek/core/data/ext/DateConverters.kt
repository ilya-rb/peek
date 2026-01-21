package com.illiarb.peek.core.data.ext

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

public fun Instant.toLocalDate(): LocalDate {
  return this.toLocalDateTime().date
}

public fun Instant.toLocalDateTime(): LocalDateTime {
  return this.toLocalDateTime(TimeZone.currentSystemDefault())
}

public fun Long.toLocalDate(): LocalDate {
  return Instant.fromEpochMilliseconds(this).toLocalDate()
}

public fun LocalDate.toEpochMilliseconds(): Long {
  return atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}
