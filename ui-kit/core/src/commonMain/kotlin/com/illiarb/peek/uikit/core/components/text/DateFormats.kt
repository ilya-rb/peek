package com.illiarb.peek.uikit.core.components.text

import androidx.compose.runtime.Composable
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.duration_hours_ago
import com.illiarb.peek.uikit.resources.duration_less_then_a_minute
import com.illiarb.peek.uikit.resources.duration_minutes_ago
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

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

  @Composable
  public fun formatTimestamp(time: Duration): String {
    return time.toComponents { hours, minutes, _, _ ->
      when {
        hours > 0 -> pluralStringResource(Res.plurals.duration_hours_ago, hours.toInt(), hours)
        minutes > 0 -> stringResource(Res.string.duration_minutes_ago, minutes)
        else -> stringResource(Res.string.duration_less_then_a_minute)
      }
    }
  }
}
