package com.illiarb.peek.features.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Anytime
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Evening
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Midday
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Morning
import com.illiarb.peek.uikit.core.components.cell.SwitchCell
import com.illiarb.peek.uikit.core.image.VectorIcon
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
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddTaskBottomSheet(
  onDismiss: () -> Unit,
  onSubmit: (
    title: String,
    isHabit: Boolean,
    timeOfDay: TimeOfDay,
    dismissSheet: Boolean,
  ) -> Unit,
) {
  var taskTitle by remember { mutableStateOf("") }
  var isHabit by remember { mutableStateOf(false) }
  var timeOfDay by remember { mutableStateOf(Morning) }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val coroutineScope = rememberCoroutineScope()

  val dismissSheet: () -> Unit = remember {
    {
      coroutineScope.launch {
        sheetState.hide()
        onDismiss()
      }
    }
  }
  val submitTask: () -> Unit = remember {
    {
      coroutineScope.launch {
        sheetState.hide()
        val selectedTimeOfDay = if (isHabit) timeOfDay else Anytime
        onSubmit(taskTitle.trim(), isHabit, selectedTimeOfDay, true)
      }
    }
  }

  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = dismissSheet,
  ) {
    BottomSheetHeader(onCloseClicked = dismissSheet)

    OutlinedTextField(
      value = taskTitle,
      onValueChange = { taskTitle = it },
      placeholder = { Text(stringResource(Res.string.tasks_add_input_hint)) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(
        onDone = {
          if (taskTitle.isNotBlank()) {
            submitTask()
          }
        }
      ),
      modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp),
    )

    SwitchCell(
      text = stringResource(Res.string.tasks_add_habit_title),
      subtitle = stringResource(Res.string.tasks_add_habit_subtitle),
      startIcon = VectorIcon(
        imageVector = Icons.Filled.EventRepeat,
        contentDescription = stringResource(Res.string.acsb_icon_task_habit),
      ),
      switchChecked = isHabit,
      onChecked = { isHabit = it },
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp),
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
        val selectedTimeOfDay = if (isHabit) timeOfDay else Anytime
        onSubmit(taskTitle.trim(), isHabit, selectedTimeOfDay, false)

        isHabit = false
        taskTitle = ""
        timeOfDay = Morning
      },
      onSubmit = submitTask,
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
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .navigationBarsPadding()
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = stringResource(Res.string.tasks_add_title),
        style = MaterialTheme.typography.titleLarge,
      )
      Spacer(Modifier.weight(1f))
      IconButton(
        onClick = onCloseClicked,
        content = {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(Res.string.acsb_action_close),
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainer)
              .padding(8.dp)
          )
        },
      )
    }
  }
}

@Composable
private fun ButtonsFooter(
  enabled: Boolean,
  onAddNext: () -> Unit,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 16.dp)
  ) {
    OutlinedButton(
      modifier = Modifier.weight(1f),
      enabled = enabled,
      onClick = onAddNext,
      content = { Text(stringResource(Res.string.tasks_add_add_next)) }
    )
    Button(
      modifier = Modifier.weight(1f),
      enabled = enabled,
      onClick = onSubmit,
      content = { Text(stringResource(Res.string.tasks_add_submit)) }
    )
  }
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
