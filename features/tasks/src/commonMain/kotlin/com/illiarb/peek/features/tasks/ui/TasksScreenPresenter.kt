package com.illiarb.peek.features.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.tasks.TasksService
import com.illiarb.peek.features.tasks.domain.HabitStatistics
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
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

internal class TasksScreenPresenter(
  private val navigator: Navigator,
  private val tasksService: TasksService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<TasksScreenContract.State> {

  @Composable
  @Suppress("CyclomaticComplexMethod")
  override fun present(): TasksScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()
    val now = rememberRetained {
      Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    var selectedDate by rememberRetained {
      mutableStateOf(now.date)
    }
    var showAddTaskSheet by rememberRetained {
      mutableStateOf(false)
    }
    var reloadTrigger by rememberRetained {
      mutableStateOf(0)
    }
    var expandedSections by rememberRetained {
      mutableStateOf(
        when (now.time.hour) {
          in 6..11 -> setOf(TimeOfDay.MORNING)
          in 12..17 -> setOf(TimeOfDay.MIDDAY)
          else -> setOf(TimeOfDay.EVENING)
        }
      )
    }
    val tasks by produceRetainedState<Async<ImmutableMap<TimeOfDay, List<Task>>>>(
      initialValue = Async.Loading,
      key1 = reloadTrigger,
      key2 = selectedDate,
    ) {
      tasksService.getTasksForDate(selectedDate)
        .mapContent {
          it.groupBy { task -> task.timeOfDay }.toImmutableMap()
        }
        .collect { value = it }
    }

    val statistics by produceRetainedState<Async<HabitStatistics>>(Async.Loading) {
      tasksService.getHabitStatistics().collect { value = it }
    }

    return TasksScreenContract.State(
      tasks = tasks,
      statistics = statistics,
      showAddTaskSheet = showAddTaskSheet,
      expandedSections = expandedSections,
      selectedDate = selectedDate,
      eventSink = { event ->
        when (event) {
          is Event.NavigateBack -> navigator.pop()
          is Event.AddTaskClicked -> showAddTaskSheet = true
          is Event.AddTaskDismissed -> showAddTaskSheet = false
          is Event.ErrorRetryClicked -> reloadTrigger++
          is Event.PreviousDayClicked -> selectedDate = selectedDate.minus(1, DateTimeUnit.DAY)
          is Event.NextDayClicked -> selectedDate = selectedDate.plus(1, DateTimeUnit.DAY)

          is Event.AddTaskSubmitted -> {
            if (event.dismissSheet) {
              showAddTaskSheet = false
            }
            coroutineScope.launch {
              createNewTask(event, selectedDate)
            }
          }

          is Event.TaskToggled -> {
            coroutineScope.launch {
              tasksService.toggleCompletion(event.task, selectedDate)
            }
          }

          is Event.TaskDeleted -> {
            coroutineScope.launch {
              deleteTask(event)
            }
          }

          is Event.SectionToggled -> {
            expandedSections = if (event.timeOfDay in expandedSections) {
              expandedSections - event.timeOfDay
            } else {
              expandedSections + event.timeOfDay
            }
          }
        }
      }
    )
  }

  private suspend fun deleteTask(event: Event.TaskDeleted) {
    tasksService.deleteTask(event.taskId).onSuccess {
      messageDispatcher.sendMessage(
        Message(
          content = getString(Res.string.tasks_task_removed),
          type = MessageType.SUCCESS,
        )
      )
    }
  }

  private suspend fun createNewTask(event: Event.AddTaskSubmitted, forDate: LocalDate) {
    val draft = TaskDraft(
      title = event.title,
      habit = event.isHabit,
      timeOfDay = event.timeOfDay,
      forDate = forDate,
    )
    tasksService.addTask(draft)
  }
}
