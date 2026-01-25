package com.illiarb.peek.features.tasks.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Anytime
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.Event
import com.illiarb.peek.features.tasks.ui.components.TaskCell
import com.illiarb.peek.features.tasks.ui.components.TimeOfDayHeader
import com.illiarb.peek.uikit.core.atom.IconCounter
import com.illiarb.peek.uikit.core.components.bottomsheet.ActionsBottomSheet
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.cell.ListHeader
import com.illiarb.peek.uikit.core.components.cell.loading.TaskLoadingCell
import com.illiarb.peek.uikit.core.components.date.DateSelector
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBar
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBarStyle
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.model.ButtonModel
import com.illiarb.peek.uikit.core.theme.UiKitColors
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_add_task
import com.illiarb.peek.uikit.resources.acsb_icon_current_streak
import com.illiarb.peek.uikit.resources.acsb_icon_tasks_empty
import com.illiarb.peek.uikit.resources.common_action_cancel
import com.illiarb.peek.uikit.resources.tasks_delete_confirm_action
import com.illiarb.peek.uikit.resources.tasks_delete_confirm_description
import com.illiarb.peek.uikit.resources.tasks_delete_confirm_title
import com.illiarb.peek.uikit.resources.tasks_empty_title
import com.illiarb.peek.uikit.resources.tasks_group_anytime
import com.illiarb.peek.uikit.resources.tasks_uncheck_confirm_action
import com.illiarb.peek.uikit.resources.tasks_uncheck_confirm_description
import com.illiarb.peek.uikit.resources.tasks_uncheck_confirm_title
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TasksScreen(
  state: TasksScreenContract.State,
  modifier: Modifier = Modifier,
) {
  val eventSink = state.eventSink

  if (state.showAddTaskSheet) {
    AddTaskBottomSheet(
      selectedDate = state.selectedDate,
      onDismiss = { eventSink(Event.AddTaskDismissed) },
      onSubmit = { draft, dismissSheet ->
        eventSink(Event.AddTaskSubmitted(draft, dismissSheet))
      },
    )
  }

  if (state.taskToUncheck != null) {
    ConfirmUncheckBottomSheet(
      onDismiss = { eventSink(Event.UncheckCancelled) },
      onConfirm = { eventSink(Event.UncheckConfirmed) },
    )
  }

  if (state.taskToDelete != null) {
    ConfirmDeleteBottomSheet(
      onDismiss = { eventSink(Event.DeleteCancelled) },
      onConfirm = { eventSink(Event.DeleteConfirmed) },
    )
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      UiKitTopAppBar(
        style = UiKitTopAppBarStyle.Centered,
        title = {
          DateSelector(
            selectedDate = state.selectedDate,
            onPreviousClicked = { eventSink(Event.PreviousDayClicked) },
            onNextClicked = { eventSink(Event.NextDayClicked) },
          )
        },
        onNavigationButtonClick = { eventSink(Event.NavigateBack) },
        actions = {
          when (val stats = state.statistics) {
            is Async.Content -> {
              if (stats.content.currentStreak > 0) {
                IconCounter(
                  icon = VectorIcon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = stringResource(Res.string.acsb_icon_current_streak),
                    tint = UiKitColors.orange,
                  ),
                  value = stats.content.currentStreak,
                )
              }
            }

            else -> Unit
          }
        },
      )
    },
    floatingActionButton = {
      val isToday = state.selectedDate == state.today

      AnimatedVisibility(
        visible = isToday,
        enter = scaleIn(),
        exit = scaleOut(),
      ) {
        FloatingActionButton(onClick = { eventSink(Event.AddTaskClicked) }) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(Res.string.acsb_action_add_task),
          )
        }
      }
    },
  ) { innerPadding ->
    AnimatedContent(
      contentKey = { state.tasks.stateKey() },
      targetState = state.tasks,
      transitionSpec = { fadeIn().togetherWith(fadeOut()) }
    ) { targetState ->
      when (targetState) {
        is Async.Loading -> {
          TasksLoading(contentPadding = innerPadding)
        }

        is Async.Error -> {
          ErrorEmptyState(modifier = Modifier.padding(innerPadding)) {
            eventSink(Event.ErrorRetryClicked)
          }
        }

        is Async.Content -> {
          if (targetState.content.isEmpty()) {
            TasksEmpty(Modifier.padding(innerPadding))
          } else {
            TasksList(
              tasks = targetState.content,
              contentPadding = innerPadding,
              expandedSections = state.expandedSections,
              selectedDate = state.selectedDate,
              today = state.today,
              onTaskToggled = { task -> eventSink(Event.TaskToggled(task)) },
              onTaskDeleted = { task -> eventSink(Event.TaskDeleted(task)) },
              onSectionToggled = { timeOfDay -> eventSink(Event.SectionToggled(timeOfDay)) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TasksLoading(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = contentPadding,
  ) {
    items(count = 5) {
      TaskLoadingCell()
    }
  }
}

@Composable
private fun TasksEmpty(
  modifier: Modifier = Modifier,
) {
  EmptyState(
    modifier = modifier,
    title = stringResource(Res.string.tasks_empty_title),
    image = VectorIcon(
      imageVector = Icons.AutoMirrored.Filled.Assignment,
      contentDescription = stringResource(Res.string.acsb_icon_tasks_empty),
    ),
  )
}

@Composable
private fun TasksList(
  contentPadding: PaddingValues,
  tasks: ImmutableMap<TimeOfDay, List<Task>>,
  expandedSections: Set<TimeOfDay>,
  today: LocalDate,
  selectedDate: LocalDate,
  onTaskToggled: (Task) -> Unit,
  onTaskDeleted: (Task) -> Unit,
  onSectionToggled: (TimeOfDay) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = contentPadding,
  ) {
    tasks.minus(Anytime)
      .filter { entry -> entry.value.isNotEmpty() }
      .forEach { (timeOfDay, sectionTasks) ->
        item(key = timeOfDay.name) {
          TimeOfDayHeader(
            modifier = Modifier.padding(vertical = 8.dp),
            timeOfDay = timeOfDay,
            isExpanded = expandedSections.contains(timeOfDay),
            allTasksCompleted = sectionTasks.all { it.completed },
            onToggle = {
              onSectionToggled(timeOfDay)
            },
          )
        }
        itemsIndexed(
          items = if (expandedSections.contains(timeOfDay)) sectionTasks else emptyList(),
          key = { _, task -> task.id },
        ) { index, task ->
          TaskCell(
            today = today,
            enabled = selectedDate >= today,
            index = index,
            itemsCount = sectionTasks.size,
            onTaskDeleted = onTaskDeleted,
            onTaskToggled = onTaskToggled,
            selectedDate = selectedDate,
            showOverdue = false,
            task = task,
          )
        }
      }

    item(key = Anytime.name) {
      ListHeader(
        title = stringResource(Res.string.tasks_group_anytime),
        modifier = Modifier.padding(top = 16.dp),
      )
    }

    val anytimeTasks = tasks[Anytime].orEmpty()
    if (anytimeTasks.isEmpty()) {
      item(key = "${Anytime.name}.empty") {
        TasksEmpty(Modifier.navigationBarsPadding())
      }
    } else {
      itemsIndexed(
        items = anytimeTasks,
        key = { _, task -> task.id },
      ) { index, task ->
        TaskCell(
          today = today,
          enabled = selectedDate >= today && task.createdForDate >= today,
          index = index,
          itemsCount = anytimeTasks.size,
          onTaskDeleted = onTaskDeleted,
          onTaskToggled = onTaskToggled,
          selectedDate = selectedDate,
          showOverdue = true,
          task = task,
        )
      }
    }
  }
}

@Composable
private fun ConfirmUncheckBottomSheet(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  ActionsBottomSheet(
    title = stringResource(Res.string.tasks_uncheck_confirm_title),
    description = stringResource(Res.string.tasks_uncheck_confirm_description),
    onDismiss = onDismiss,
    primaryButton = ButtonModel(
      text = stringResource(Res.string.tasks_uncheck_confirm_action),
      onClick = onConfirm,
    ),
    secondaryButton = ButtonModel(
      text = stringResource(Res.string.common_action_cancel),
      onClick = onDismiss,
    ),
  )
}

@Composable
private fun ConfirmDeleteBottomSheet(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  ActionsBottomSheet(
    title = stringResource(Res.string.tasks_delete_confirm_title),
    description = stringResource(Res.string.tasks_delete_confirm_description),
    onDismiss = onDismiss,
    primaryButton = ButtonModel(
      text = stringResource(Res.string.tasks_delete_confirm_action),
      onClick = onConfirm,
    ),
    secondaryButton = ButtonModel(
      text = stringResource(Res.string.common_action_cancel),
      onClick = onDismiss,
    ),
  )
}
