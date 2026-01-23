package com.illiarb.peek.uikit.core.components.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.duration_hours_ago
import com.illiarb.peek.uikit.resources.duration_less_then_a_minute
import com.illiarb.peek.uikit.resources.duration_minutes_ago
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
    val monthShort = month.take(3).uppercase()

    return buildString {
      append(monthShort)
      append(" ")
      append(dateToFormat.day)

      if (dateToFormat.year != currentDate.year) {
        append(" ")
        append(dateToFormat.year)
      }
    }
  }
}

@Preview
@Composable
private fun DateFormatsTimestampPreview() {
  PreviewTheme(darkMode = false) {
    Text(text = DateFormats.formatTimestamp(2.hours + 30.minutes))
  }
}

@Preview
@Composable
private fun DateFormatsTimestampMinutesPreview() {
  PreviewTheme(darkMode = false) {
    Text(text = DateFormats.formatTimestamp(15.minutes))
  }
}

@Preview
@Composable
private fun DateFormatsTimestampLessThanMinutePreview() {
  PreviewTheme(darkMode = false) {
    Text(text = DateFormats.formatTimestamp(30.seconds))
  }
}

@Preview
@Composable
private fun DateFormatsDatePreview() {
  PreviewTheme(darkMode = false) {
    val currentDate = LocalDate(2026, 1, 23)
    val dateToFormat = LocalDate(2026, 1, 15)
    Text(text = DateFormats.formatDate(dateToFormat, currentDate))
  }
}

@Preview
@Composable
private fun DateFormatsDateWithYearPreview() {
  PreviewTheme(darkMode = false) {
    Text(
      text = DateFormats.formatDate(
        dateToFormat = LocalDate(2025, 12, 25),
        currentDate = LocalDate(2026, 1, 23),
      )
    )
  }
}
