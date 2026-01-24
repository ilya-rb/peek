package com.illiarb.peek.features.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.illiarb.peek.features.tasks.domain.TaskDraft
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Anytime
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Evening
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Midday
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Morning
import com.illiarb.peek.uikit.core.atom.HorizontalButtons
import com.illiarb.peek.uikit.core.components.cell.ListHeader
import com.illiarb.peek.uikit.core.components.cell.RowCell
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.EndContent
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.StartContent
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.model.ButtonModel
import com.illiarb.peek.uikit.core.model.IconStyle
import com.illiarb.peek.uikit.core.model.TextModel
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_close
import com.illiarb.peek.uikit.resources.acsb_icon_task_habit
import com.illiarb.peek.uikit.resources.tasks_add_add_next
import com.illiarb.peek.uikit.resources.tasks_add_habit_subtitle
import com.illiarb.peek.uikit.resources.tasks_add_habit_title
import com.illiarb.peek.uikit.resources.tasks_add_input_hint
import com.illiarb.peek.uikit.resources.tasks_add_submit
import com.illiarb.peek.uikit.resources.tasks_add_time_of_day_title
import com.illiarb.peek.uikit.resources.tasks_add_title
import com.illiarb.peek.uikit.resources.tasks_time_of_day_evening
import com.illiarb.peek.uikit.resources.tasks_time_of_day_midday
import com.illiarb.peek.uikit.resources.tasks_time_of_day_morning
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddTaskBottomSheet(
  selectedDate: LocalDate,
  onDismiss: () -> Unit,
  onSubmit: (draft: TaskDraft, dismissSheet: Boolean) -> Unit,
) {
  var taskTitle by remember { mutableStateOf("") }
  var isHabit by remember { mutableStateOf(false) }
  var timeOfDay by remember { mutableStateOf(Morning) }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val coroutineScope = rememberCoroutineScope()

  fun withSheetClose(onClosed: () -> Unit) {
    coroutineScope.launch {
      sheetState.hide()
      onClosed()
    }
  }

  fun createDraft(): TaskDraft {
    return TaskDraft(
      title = taskTitle.trim(),
      habit = isHabit,
      timeOfDay = if (isHabit) timeOfDay else Anytime,
      forDate = selectedDate,
    )
  }

  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = {
      withSheetClose { onDismiss() }
    },
  ) {
    BottomSheetHeader(
      onCloseClicked = {
        withSheetClose { onDismiss() }
      }
    )

    OutlinedTextField(
      value = taskTitle,
      onValueChange = { taskTitle = it },
      placeholder = { Text(stringResource(Res.string.tasks_add_input_hint)) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(
        onDone = {
          if (taskTitle.isNotBlank()) {
            withSheetClose { onSubmit(createDraft(), true) }
          }
        }
      ),
      modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp),
    )

    RowCell(
      title = TextModel(stringResource(Res.string.tasks_add_habit_title)),
      subtitle = TextModel(stringResource(Res.string.tasks_add_habit_subtitle)),
      startContent = StartContent.Icon(
        VectorIcon(
          imageVector = Icons.Filled.EventRepeat,
          contentDescription = stringResource(Res.string.acsb_icon_task_habit),
        )
      ),
      endContent = EndContent.Switch(isHabit),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .clickable { isHabit = !isHabit }
    )

    AnimatedVisibility(
      visible = isHabit,
      enter = expandVertically(),
      exit = shrinkVertically(),
    ) {
      HabitTimeSelector(timeOfDay, onNewSelected = { newSelection -> timeOfDay = newSelection })
    }

    ButtonsFooter(
      enabled = taskTitle.isNotBlank(),
      onAddNext = {
        onSubmit(createDraft(), false)

        isHabit = false
        taskTitle = ""
        timeOfDay = Morning
      },
      onSubmit = {
        withSheetClose {
          onSubmit(createDraft(), true)
        }
      },
    )
  }
}

@Composable
private fun HabitTimeSelector(
  currentSelection: TimeOfDay,
  onNewSelected: (TimeOfDay) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp)
  ) {
    Text(
      text = stringResource(Res.string.tasks_add_time_of_day_title),
      style = MaterialTheme.typography.labelLarge,
      modifier = Modifier.padding(bottom = 8.dp),
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
      val entries = TimeOfDay.entries.minus(Anytime)

      entries.forEachIndexed { index, entry ->
        SegmentedButton(
          selected = currentSelection == entry,
          onClick = { onNewSelected(entry) },
          shape = SegmentedButtonDefaults.itemShape(index = index, count = entries.size),
        ) {
          Text(entry.title())
        }
      }
    }
  }
}

@Composable
private fun BottomSheetHeader(
  modifier: Modifier = Modifier,
  onCloseClicked: () -> Unit,
) {
  ListHeader(
    modifier = modifier,
    title = stringResource(Res.string.tasks_add_title),
    onEndIconClick = onCloseClicked,
    endIcon = VectorIcon(
      imageVector = Icons.Filled.Close,
      contentDescription = stringResource(Res.string.acsb_action_close),
      style = IconStyle.CircleBackground,
    ),
  )
}

@Composable
private fun ButtonsFooter(
  enabled: Boolean,
  onAddNext: () -> Unit,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  HorizontalButtons(
    secondary = ButtonModel(
      text = stringResource(Res.string.tasks_add_add_next),
      enabled = enabled,
      onClick = onAddNext,
    ),
    primary = ButtonModel(
      text = stringResource(Res.string.tasks_add_submit),
      enabled = enabled,
      onClick = onSubmit,
    ),
    modifier = modifier
      .padding(horizontal = 16.dp)
      .padding(top = 24.dp, bottom = 16.dp),
  )
}

@Composable
private fun TimeOfDay.title(): String {
  return when (this) {
    Morning -> stringResource(Res.string.tasks_time_of_day_morning)
    Midday -> stringResource(Res.string.tasks_time_of_day_midday)
    Evening -> stringResource(Res.string.tasks_time_of_day_evening)
    Anytime -> throw IllegalArgumentException("Anytime is not supported")
  }
}
