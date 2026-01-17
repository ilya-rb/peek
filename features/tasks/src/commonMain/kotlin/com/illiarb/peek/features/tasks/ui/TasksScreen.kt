package com.illiarb.peek.features.tasks.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.Event
import com.illiarb.peek.uikit.core.components.cell.CheckableListItem
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.cell.SwipeToDeleteContainer
import com.illiarb.peek.uikit.core.components.cell.TaskLoadingCell
import com.illiarb.peek.uikit.core.model.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_add_task
import com.illiarb.peek.uikit.resources.acsb_icon_tasks_empty
import com.illiarb.peek.uikit.resources.acsb_navigation_back
import com.illiarb.peek.uikit.resources.tasks_empty_add
import com.illiarb.peek.uikit.resources.tasks_empty_title
import com.illiarb.peek.uikit.resources.tasks_group_today
import com.illiarb.peek.uikit.resources.tasks_title
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

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
      onSubmit = { title ->
        eventSink(Event.AddTaskSubmitted(title))
      },
    )
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(text = stringResource(Res.string.tasks_title))
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
            TasksEmpty(innerPadding) {
              eventSink(Event.AddTaskClicked)
            }
          } else {
            TasksList(
              tasks = targetState.content,
              contentPadding = innerPadding,
              onTaskToggled = { task -> eventSink(Event.TaskToggled(task)) },
              onTaskDeleted = { taskId -> eventSink(Event.TaskDeleted(taskId)) },
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
private fun TasksEmpty(contentPadding: PaddingValues, onAddTaskClicked: () -> Unit) {
  EmptyState(
    modifier = Modifier.padding(contentPadding),
    title = stringResource(Res.string.tasks_empty_title),
    buttonText = stringResource(Res.string.tasks_empty_add),
    buttonIcon = VectorIcon(
      imageVector = Icons.Filled.Add,
      contentDescription = stringResource(Res.string.acsb_action_add_task),
    ),
    onButtonClick = onAddTaskClicked,
    image = VectorIcon(
      imageVector = Icons.Filled.ListAlt,
      contentDescription = stringResource(Res.string.acsb_icon_tasks_empty),
    ),
  )
}

@Composable
private fun TasksList(
  tasks: ImmutableList<Task>,
  contentPadding: PaddingValues,
  onTaskToggled: (Task) -> Unit,
  onTaskDeleted: (String) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = contentPadding,
  ) {
    item { TasksHeader() }
    items(
      items = tasks,
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

@Composable
private fun TasksHeader() {
  Text(
    text = stringResource(Res.string.tasks_group_today),
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
  )
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
