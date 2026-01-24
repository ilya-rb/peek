package com.illiarb.peek.features.tasks.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Anytime
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Evening
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Midday
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Morning
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.Event
import com.illiarb.peek.uikit.core.atom.BoxListItemContainer
import com.illiarb.peek.uikit.core.components.bottomsheet.ActionsBottomSheet
import com.illiarb.peek.uikit.core.components.bottomsheet.ButtonModel
import com.illiarb.peek.uikit.core.components.cell.CheckableRow
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.cell.SwipeToDeleteContainer
import com.illiarb.peek.uikit.core.components.cell.loading.TaskLoadingCell
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBar
import com.illiarb.peek.uikit.core.components.text.DateFormats
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.theme.UiKitColors
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_add_task
import com.illiarb.peek.uikit.resources.acsb_icon_collapse
import com.illiarb.peek.uikit.resources.acsb_icon_current_streak
import com.illiarb.peek.uikit.resources.acsb_icon_expand
import com.illiarb.peek.uikit.resources.acsb_icon_next_day
import com.illiarb.peek.uikit.resources.acsb_icon_previous_day
import com.illiarb.peek.uikit.resources.acsb_icon_task_overdue
import com.illiarb.peek.uikit.resources.acsb_icon_tasks_empty
import com.illiarb.peek.uikit.resources.common_action_cancel
import com.illiarb.peek.uikit.resources.tasks_empty_title
import com.illiarb.peek.uikit.resources.tasks_group_anytime
import com.illiarb.peek.uikit.resources.tasks_group_routines
import com.illiarb.peek.uikit.resources.tasks_section_completed
import com.illiarb.peek.uikit.resources.tasks_section_evening
import com.illiarb.peek.uikit.resources.tasks_section_midday
import com.illiarb.peek.uikit.resources.tasks_section_morning
import com.illiarb.peek.uikit.resources.tasks_today_title
import com.illiarb.peek.uikit.resources.tasks_uncheck_confirm_action
import com.illiarb.peek.uikit.resources.tasks_uncheck_confirm_description
import com.illiarb.peek.uikit.resources.tasks_uncheck_confirm_title
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
internal fun TasksScreen(
  state: TasksScreenContract.State,
  modifier: Modifier = Modifier,
) {
  val eventSink = state.eventSink

  if (state.showAddTaskSheet) {
    AddTaskBottomSheet(
      onDismiss = {
        eventSink(Event.AddTaskDismissed)
      },
      onSubmit = { title, isHabit, timeOfDay, dismissSheet ->
        eventSink(Event.AddTaskSubmitted(title, isHabit, timeOfDay, dismissSheet))
      },
    )
  }

  if (state.taskToUncheck != null) {
    ActionsBottomSheet(
      title = stringResource(Res.string.tasks_uncheck_confirm_title),
      description = stringResource(Res.string.tasks_uncheck_confirm_description),
      onDismiss = { eventSink(Event.UncheckCancelled) },
      primaryButton = ButtonModel(
        stringResource(Res.string.tasks_uncheck_confirm_action),
        onClick = {
          eventSink(Event.UncheckConfirmed)
        }
      ),
      secondaryButton = ButtonModel(
        stringResource(Res.string.common_action_cancel),
        onClick = {
          eventSink(Event.UncheckCancelled)
        }
      ),
    )
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      UiKitTopAppBar(
        title = {
          DateSelector(
            selectedDate = state.selectedDate,
            onPreviousClicked = { eventSink(Event.PreviousDayClicked) },
            onNextClicked = { eventSink(Event.NextDayClicked) },
          )
        },
        onNavigationButtonClick = {
          eventSink(Event.NavigateBack)
        },
        actions = {
          when (val stats = state.statistics) {
            is Async.Content -> {
              if (stats.content.currentStreak > 0) {
                StreakCounter(streak = stats.content.currentStreak)
              }
            }

            else -> Unit
          }
        },
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = { eventSink(Event.AddTaskClicked) }) {
        Icon(
          imageVector = Icons.Filled.Add,
          contentDescription = stringResource(Res.string.acsb_action_add_task),
        )
      }
    },
  ) { innerPadding ->
    AnimatedContent(
      contentKey = { state.tasks.stateKey() },
      targetState = state.tasks,
      transitionSpec = { fadeIn().togetherWith(fadeOut()) }
    ) { targetState ->
      when (targetState) {
        is Async.Error -> {
          ErrorEmptyState(modifier = Modifier.padding(innerPadding)) {
            eventSink(Event.ErrorRetryClicked)
          }
        }

        is Async.Content -> {
          if (targetState.content.isEmpty()) {
            TasksEmpty(innerPadding)
          } else {
            TasksList(
              tasks = targetState.content,
              contentPadding = innerPadding,
              expandedSections = state.expandedSections,
              selectedDate = state.selectedDate,
              today = state.today,
              onTaskToggled = { task -> eventSink(Event.TaskToggled(task)) },
              onTaskDeleted = { taskId -> eventSink(Event.TaskDeleted(taskId)) },
              onSectionToggled = { timeOfDay -> eventSink(Event.SectionToggled(timeOfDay)) },
            )
          }
        }

        is Async.Loading -> {
          TasksLoading(contentPadding = innerPadding)
        }
      }
    }
  }
}

@Composable
private fun TasksEmpty(contentPadding: PaddingValues) {
  EmptyState(
    modifier = Modifier.padding(contentPadding),
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
  onTaskDeleted: (String) -> Unit,
  onSectionToggled: (TimeOfDay) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = contentPadding,
  ) {
    item(key = "routines_header") {
      RoutinesHeader()
    }

    tasks.minus(Anytime).forEach { (timeOfDay, sectionTasks) ->
      if (sectionTasks.isNotEmpty()) {
        item(key = "header_${timeOfDay.name}") {
          TimeOfDaySectionHeader(
            timeOfDay = timeOfDay,
            isExpanded = expandedSections.contains(timeOfDay),
            allTasksCompleted = sectionTasks.all { it.completed },
            onToggle = { onSectionToggled(timeOfDay) },
          )
        }

        itemsIndexed(
          items = if (expandedSections.contains(timeOfDay)) sectionTasks else emptyList(),
          key = { _, task -> task.id },
        ) { index, task ->
          val enabled = selectedDate >= today
          SwipeToDeleteContainer(
            onDelete = { onTaskDeleted(task.id) },
            enabled = enabled && !task.completed,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .animateItem(fadeInSpec = null),
          ) {
            BoxListItemContainer(index, sectionTasks.size) {
              CheckableRow(
                title = task.title,
                checked = task.completed,
                onCheckedChange = { onTaskToggled(task) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
              )
            }
          }
        }
      }
    }

    item(key = "tasks_header") {
      TasksHeader()
    }

    val anytimeTasks = tasks[Anytime].orEmpty()
    if (anytimeTasks.isEmpty()) {
      item(key = "tasks_empty") {
        TasksEmpty(PaddingValues())
      }
    } else {
      items(
        items = anytimeTasks,
        key = { it.id },
      ) { task ->
        val enabled = selectedDate >= today && task.createdForDate >= today
        SwipeToDeleteContainer(
          onDelete = { onTaskDeleted(task.id) },
          enabled = enabled && !task.completed,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .animateItem(fadeInSpec = null),
        ) {
          CheckableRow(
            title = task.title,
            checked = task.completed,
            subtitle = if (selectedDate == task.createdForDate) {
              null
            } else {
              DateFormats.formatDate(task.createdForDate, today)
            },
            onCheckedChange = { onTaskToggled(task) },
            enabled = enabled,
            trailingContent = {
              if (task.createdForDate < selectedDate) {
                Icon(
                  imageVector = Icons.Filled.AssignmentLate,
                  contentDescription = stringResource(Res.string.acsb_icon_task_overdue),
                  tint = MaterialTheme.colorScheme.error,
                )
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(MaterialTheme.colorScheme.surfaceContainer),
          )
        }
      }
    }
  }
}

@Composable
private fun RoutinesHeader(modifier: Modifier = Modifier) {
  Text(
    text = stringResource(Res.string.tasks_group_routines),
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier.padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 16.dp),
  )
}

@Composable
private fun TasksHeader(modifier: Modifier = Modifier) {
  Text(
    text = stringResource(Res.string.tasks_group_anytime),
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier.padding(horizontal = 16.dp).padding(top = 24.dp, bottom = 16.dp),
  )
}

@Composable
private fun TimeOfDaySectionHeader(
  timeOfDay: TimeOfDay,
  isExpanded: Boolean,
  allTasksCompleted: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clickable { onToggle() },
  ) {
    Icon(
      imageVector = timeOfDay.getIcon(),
      contentDescription = null,
      modifier = Modifier.padding(start = 16.dp).size(20.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = stringResource(timeOfDay.getSectionTitle()),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(start = 8.dp).padding(vertical = 16.dp),
    )

    if (allTasksCompleted) {
      Text(
        text = " · ${stringResource(Res.string.tasks_section_completed)}",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(vertical = 16.dp),
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    Icon(
      imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
      modifier = Modifier.padding(end = 16.dp).size(20.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      contentDescription = if (isExpanded) {
        stringResource(Res.string.acsb_icon_collapse)
      } else {
        stringResource(Res.string.acsb_icon_expand)
      },
    )
  }
}

@Composable
private fun DateSelector(
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

@Composable
private fun StreakCounter(
  streak: Int,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.padding(end = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Filled.LocalFireDepartment,
      contentDescription = stringResource(Res.string.acsb_icon_current_streak),
      tint = UiKitColors.orange,
    )
    Text(
      text = streak.toString(),
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
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

private fun TimeOfDay.getSectionTitle() = when (this) {
  Morning -> Res.string.tasks_section_morning
  Midday -> Res.string.tasks_section_midday
  Evening -> Res.string.tasks_section_evening
  Anytime -> throw IllegalArgumentException("Anytime not supported")
}

private fun TimeOfDay.getIcon(): ImageVector = when (this) {
  Morning -> Icons.Filled.WbTwilight
  Midday -> Icons.Filled.WbSunny
  Evening -> Icons.Filled.Brightness3
  Anytime -> throw IllegalArgumentException("Anytime not supported")
}
