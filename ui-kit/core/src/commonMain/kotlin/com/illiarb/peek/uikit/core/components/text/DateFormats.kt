package com.illiarb.peek.uikit.core.components.text

import androidx.compose.runtime.Composable
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.duration_hours_ago
import com.illiarb.peek.uikit.resources.duration_less_then_a_minute
import com.illiarb.peek.uikit.resources.duration_minutes_ago
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

public object DateFormats {

  @Composable
  public fun formatTimestamp(time: Duration): String {
    return time.toComponents { hours, minutes, _, _ ->
      when {
        hours > 0 -> pluralStringResource(Res.plurals.duration_hours_ago, hours.toInt(), hours)
        minutes > 1 -> stringResource(Res.string.duration_minutes_ago, minutes)
        else -> stringResource(Res.string.duration_less_then_a_minute)
      }
    }
  }

  public fun formatDate(dateToFormat: LocalDate, currentDate: LocalDate): String {
    val month = dateToFormat.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val monthShort = month.take(3)

    return buildString {
      append(monthShort)
      append(dateToFormat.day)

      if (dateToFormat.year != currentDate.year) {
        append(dateToFormat.year)
      }
    }
  }
}
