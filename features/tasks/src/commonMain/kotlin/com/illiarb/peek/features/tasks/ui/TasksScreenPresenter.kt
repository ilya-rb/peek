package com.illiarb.peek.features.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.tasks.TasksService
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskDraft
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.Event
import com.illiarb.peek.uikit.messages.Message
import com.illiarb.peek.uikit.messages.MessageDispatcher
import com.illiarb.peek.uikit.messages.MessageType
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.tasks_task_removed
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

internal class TasksScreenPresenter(
  private val navigator: Navigator,
  private val tasksService: TasksService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<TasksScreenContract.State> {

  @Composable
  override fun present(): TasksScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()
    val today = Clock.System.now()
      .toLocalDateTime(TimeZone.currentSystemDefault())
      .date

    var showAddTaskSheet by rememberRetained {
      mutableStateOf(false)
    }
    var reloadTrigger by rememberRetained {
      mutableStateOf(0)
    }

    val tasks by produceRetainedState<Async<ImmutableList<Task>>>(
      initialValue = Async.Loading,
      key1 = reloadTrigger,
    ) {
      tasksService.getTasksForDate(today)
        .mapContent { it.toImmutableList() }
        .collect { value = it }
    }

    return TasksScreenContract.State(
      tasks = tasks,
      showAddTaskSheet = showAddTaskSheet,
      eventSink = { event ->
        when (event) {
          is Event.NavigateBack -> {
            navigator.pop()
          }

          is Event.AddTaskClicked -> {
            showAddTaskSheet = true
          }

          is Event.AddTaskDismissed -> {
            showAddTaskSheet = false
          }

          is Event.ErrorRetryClicked -> {
            reloadTrigger++
          }

          is Event.AddTaskSubmitted -> {
            coroutineScope.launch {
              val draft = TaskDraft(
                title = event.title,
                habit = false,
                timeOfDay = TimeOfDay.ANYTIME,
                forDate = today,
              )
              tasksService.addTask(draft)
            }
          }

          is Event.TaskToggled -> {
            coroutineScope.launch {
              tasksService.toggleCompletion(event.task, today)
            }
          }

          is Event.TaskDeleted -> {
            coroutineScope.launch {
              tasksService.deleteTask(event.taskId)

              messageDispatcher.sendMessage(
                Message(
                  content = getString(Res.string.tasks_task_removed),
                  type = MessageType.SUCCESS,
                )
              )
            }
          }
        }
      }
    )
  }
}
