package com.illiarb.peek.uikit.core.components.date

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitShapes
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_next_day
import com.illiarb.peek.uikit.resources.acsb_icon_previous_day
import com.illiarb.peek.uikit.resources.tasks_today_title
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
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

  var currentDate by remember { mutableStateOf(selectedDate) }
  val animationDirection = remember(selectedDate) {
    when {
      selectedDate < currentDate -> AnimationDirection.LEFT_TO_RIGHT
      else -> AnimationDirection.RIGHT_TO_LEFT
    }.also {
      currentDate = selectedDate
    }
  }

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
    Surface(
      shape = UiKitShapes.large,
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
      AnimatedContent(
        label = "DateTextAnimation",
        targetState = dateText,
        transitionSpec = {
          when (animationDirection) {
            AnimationDirection.LEFT_TO_RIGHT -> {
              slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }

            AnimationDirection.RIGHT_TO_LEFT -> {
              slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            }
          }
        },
      ) { text ->
        Text(
          modifier = Modifier.padding(8.dp),
          text = text,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
      }
    }
    IconButton(
      onClick = onNextClicked,
      enabled = !isToday,
    ) {
      Icon(
        imageVector = Icons.Filled.ChevronRight,
        contentDescription = stringResource(Res.string.acsb_icon_next_day),
        modifier = Modifier.padding(horizontal = 4.dp),
      )
    }
  }
}

private enum class AnimationDirection {
  LEFT_TO_RIGHT,
  RIGHT_TO_LEFT,
}

@Preview
@Composable
private fun DateSelectorTodayPreviewLight() {
  PreviewTheme(darkMode = false) {
    val today = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .date

    DateSelector(
      selectedDate = today,
      onPreviousClicked = {},
      onNextClicked = {},
    )
  }
}

@Preview
@Composable
private fun DateSelectorTodayPreviewDark() {
  PreviewTheme(darkMode = true) {
    val today = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .date

    DateSelector(
      selectedDate = today,
      onPreviousClicked = {},
      onNextClicked = {},
    )
  }
}

@Preview
@Composable
private fun DateSelectorPastDatePreviewLight() {
  PreviewTheme(darkMode = false) {
    val pastDate = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .date
      .minus(3, DAY)

    DateSelector(
      selectedDate = pastDate,
      onPreviousClicked = {},
      onNextClicked = {},
    )
  }
}

@Preview
@Composable
private fun DateSelectorPastDatePreviewDark() {
  PreviewTheme(darkMode = true) {
    val pastDate = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .date
      .minus(3, DAY)

    DateSelector(
      selectedDate = pastDate,
      onPreviousClicked = {},
      onNextClicked = {},
    )
  }
}
