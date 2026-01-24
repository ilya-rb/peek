package com.illiarb.peek.uikit.core.components.date

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_next_day
import com.illiarb.peek.uikit.resources.acsb_icon_previous_day
import com.illiarb.peek.uikit.resources.tasks_today_title
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
public fun DateSelector(
  selectedDate: LocalDate,
  onPreviousClicked: () -> Unit,
  onNextClicked: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val today = Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .date

  val isToday = selectedDate == today
  val dateText = if (isToday) {
    stringResource(Res.string.tasks_today_title).uppercase()
  } else {
    DateFormats.formatDate(selectedDate, today)
  }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(onClick = onPreviousClicked) {
      Icon(
        imageVector = Icons.Filled.ChevronLeft,
        contentDescription = stringResource(Res.string.acsb_icon_previous_day),
      )
    }
    Text(
      text = dateText,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
    )
    IconButton(
      onClick = onNextClicked,
      enabled = !isToday,
    ) {
      Icon(
        imageVector = Icons.Filled.ChevronRight,
        contentDescription = stringResource(Res.string.acsb_icon_next_day),
      )
    }
  }
}
