package com.illiarb.peek.core.data.ext

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

public fun Instant.toLocalDate(): LocalDate {
  return this.toLocalDateTime().date
}

public fun Instant.toLocalDateTime(): LocalDateTime {
  return this.toLocalDateTime(TimeZone.UTC)
}

public fun Long.toLocalDate(): LocalDate {
  return Instant.fromEpochMilliseconds(this).toLocalDate()
}

public fun Long.toLocalDateTime(): LocalDateTime {
  return Instant.fromEpochMilliseconds(this).toLocalDateTime()
}

public fun LocalDateTime.toEpochMilliseconds(): Long {
  return this.toInstant(TimeZone.UTC).toEpochMilliseconds()
}

public fun LocalDate.toEpochMilliseconds(): Long {
  return atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}
