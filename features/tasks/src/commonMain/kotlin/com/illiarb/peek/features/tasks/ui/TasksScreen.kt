package com.illiarb.peek.features.tasks.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.Event
import com.illiarb.peek.uikit.core.components.cell.CheckableListItem
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.cell.SwipeToDeleteContainer
import com.illiarb.peek.uikit.core.components.cell.TaskLoadingCell
import com.illiarb.peek.uikit.core.components.text.DateFormats
import com.illiarb.peek.uikit.core.model.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_add_task
import com.illiarb.peek.uikit.resources.acsb_icon_collapse
import com.illiarb.peek.uikit.resources.acsb_icon_expand
import com.illiarb.peek.uikit.resources.acsb_icon_next_day
import com.illiarb.peek.uikit.resources.acsb_icon_previous_day
import com.illiarb.peek.uikit.resources.acsb_icon_tasks_empty
import com.illiarb.peek.uikit.resources.acsb_navigation_back
import com.illiarb.peek.uikit.resources.tasks_empty_title
import com.illiarb.peek.uikit.resources.tasks_group_today
import com.illiarb.peek.uikit.resources.tasks_section_evening
import com.illiarb.peek.uikit.resources.tasks_section_midday
import com.illiarb.peek.uikit.resources.tasks_section_morning
import com.illiarb.peek.uikit.resources.tasks_title
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

  Scaffold(
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = stringResource(Res.string.tasks_title),
              style = MaterialTheme.typography.titleSmall,
            )
            DateSelector(
              selectedDate = state.selectedDate,
              onPreviousClicked = { eventSink(Event.PreviousDayClicked) },
              onNextClicked = { eventSink(Event.NextDayClicked) },
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = { eventSink(Event.NavigateBack) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
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
  tasks: ImmutableMap<TimeOfDay, List<Task>>,
  contentPadding: PaddingValues,
  expandedSections: Set<TimeOfDay>,
  onTaskToggled: (Task) -> Unit,
  onTaskDeleted: (String) -> Unit,
  onSectionToggled: (TimeOfDay) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = contentPadding,
  ) {
    tasks.forEach { (timeOfDay, sectionTasks) ->
      if (sectionTasks.isNotEmpty()) {
        item(key = "header_${timeOfDay.name}") {
          TimeOfDaySectionHeader(
            timeOfDay = timeOfDay,
            isExpanded = expandedSections.contains(timeOfDay),
            allTasksCompleted = sectionTasks.all { it.completed },
            onToggle = { onSectionToggled(timeOfDay) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        itemsIndexed(
          items = if (expandedSections.contains(timeOfDay)) sectionTasks else emptyList(),
          key = { _, task -> task.id },
        ) { index, task ->
          SwipeToDeleteContainer(
            onDelete = { onTaskDeleted(task.id) },
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .animateItem(fadeInSpec = null),
          ) {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = groupShape(index, sectionTasks.size),
              color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
              CheckableListItem(
                text = task.title,
                checked = task.completed,
                onCheckedChange = { onTaskToggled(task) },
                modifier = Modifier.fillMaxWidth(),
              )
            }
          }
        }
      }
    }

    item {
      TasksHeader()
    }

    val anytimeTasks = tasks[TimeOfDay.ANYTIME].orEmpty()
    if (anytimeTasks.isEmpty()) {
      item {
        TasksEmpty(PaddingValues())
      }
    } else {
      items(
        items = anytimeTasks,
        key = { it.id },
      ) { task ->
        SwipeToDeleteContainer(
          onDelete = { onTaskDeleted(task.id) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .animateItem(fadeInSpec = null),
        ) {
          CheckableListItem(
            text = task.title,
            checked = task.completed,
            onCheckedChange = { onTaskToggled(task) },
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
private fun TasksHeader() {
  Text(
    text = stringResource(Res.string.tasks_group_today),
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
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
      modifier = Modifier.size(20.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = stringResource(timeOfDay.getSectionTitle()),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(start = 8.dp),
    )

    Spacer(modifier = Modifier.weight(1f))

    if (allTasksCompleted) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        modifier = Modifier.size(20.dp).padding(end = 4.dp),
        tint = MaterialTheme.colorScheme.primary,
      )
    }

    Icon(
      imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
      modifier = Modifier.size(20.dp),
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
    stringResource(Res.string.tasks_group_today)
  } else {
    DateFormats.formatDate(selectedDate)
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
private fun TasksLoading(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = contentPadding,
  ) {
    item { TasksHeader() }
    items(count = 5) { TaskLoadingCell() }
  }
}

private fun TimeOfDay.getSectionTitle() = when (this) {
  TimeOfDay.MORNING -> Res.string.tasks_section_morning
  TimeOfDay.MIDDAY -> Res.string.tasks_section_midday
  TimeOfDay.EVENING -> Res.string.tasks_section_evening
  TimeOfDay.ANYTIME -> throw IllegalArgumentException("Anytime not supported")
}

private fun TimeOfDay.getIcon(): ImageVector = when (this) {
  TimeOfDay.MORNING -> Icons.Filled.WbTwilight
  TimeOfDay.MIDDAY -> Icons.Filled.WbSunny
  TimeOfDay.EVENING -> Icons.Filled.Brightness3
  TimeOfDay.ANYTIME -> throw IllegalArgumentException("Anytime not supported")
}

private fun groupShape(position: Int, itemsCount: Int): Shape {
  return when {
    itemsCount == 1 -> RoundedCornerShape(16.dp)
    position == 0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    position == itemsCount - 1 -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    else -> RoundedCornerShape(0.dp)
  }
}
