package com.illiarb.peek.features.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastAny
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.ext.toLocalDateTime
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
import com.illiarb.peek.uikit.resources.tasks_create_error
import com.illiarb.peek.uikit.resources.tasks_delete_error
import com.illiarb.peek.uikit.resources.tasks_task_removed
import com.illiarb.peek.uikit.resources.tasks_update_error
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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
      Clock.System.now().toLocalDateTime()
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
      mutableStateOf<Set<TimeOfDay>?>(null)
    }
    var taskToUncheck by rememberRetained {
      mutableStateOf<Task?>(null)
    }
    val tasks by produceRetainedState<Async<ImmutableMap<TimeOfDay, List<Task>>>>(
      initialValue = Async.Loading,
      key1 = reloadTrigger,
      key2 = selectedDate,
    ) {
      val isToday = selectedDate == now.date
      val flow = if (isToday) {
        tasksService.getTasksForToday()
      } else {
        tasksService.getTasksFor(selectedDate)
      }
      flow.mapContent { tasks ->
        tasks.groupBy { it.timeOfDay }.toImmutableMap()
      }.collect {
        value = it

        if (expandedSections == null && it is Async.Content) {
          expandedSections = getInitialExpandedSections(now.time.hour, it.content)
        }
      }
    }

    val statistics by produceRetainedState<Async<HabitStatistics>>(Async.Loading) {
      tasksService.getHabitsStatistics().collect { value = it }
    }

    return TasksScreenContract.State(
      tasks = tasks,
      statistics = statistics,
      showAddTaskSheet = showAddTaskSheet,
      expandedSections = expandedSections.orEmpty(),
      selectedDate = selectedDate,
      today = now.date,
      taskToUncheck = taskToUncheck,
      eventSink = { event ->
        when (event) {
          is Event.NavigateBack -> navigator.pop()
          is Event.AddTaskClicked -> showAddTaskSheet = true
          is Event.AddTaskDismissed -> showAddTaskSheet = false
          is Event.ErrorRetryClicked -> reloadTrigger++
          is Event.PreviousDayClicked -> selectedDate = selectedDate.minus(1, DAY)
          is Event.NextDayClicked -> selectedDate = selectedDate.plus(1, DAY)

          is Event.AddTaskSubmitted -> {
            if (event.dismissSheet) {
              showAddTaskSheet = false
            }
            coroutineScope.launch {
              val draft = TaskDraft(
                title = event.title,
                habit = event.isHabit,
                timeOfDay = event.timeOfDay,
                forDate = selectedDate,
              )
              tasksService.createTask(draft).onFailure {
                messageDispatcher.sendMessage(
                  Message(
                    content = getString(Res.string.tasks_create_error),
                    type = MessageType.ERROR,
                  )
                )
              }
            }
          }

          is Event.TaskToggled -> {
            if (event.task.completed) {
              taskToUncheck = event.task
            } else {
              coroutineScope.launch {
                tasksService.toggleCompletion(event.task, selectedDate).onFailure {
                  messageDispatcher.sendMessage(
                    Message(
                      content = getString(Res.string.tasks_update_error),
                      type = MessageType.ERROR,
                    )
                  )
                }
              }
            }
          }

          is Event.UncheckConfirmationRequested -> {
            taskToUncheck = event.task
          }

          is Event.UncheckConfirmed -> {
            val task = taskToUncheck
            taskToUncheck = null
            if (task != null) {
              coroutineScope.launch {
                tasksService.toggleCompletion(task, selectedDate).onFailure {
                  messageDispatcher.sendMessage(
                    Message(
                      content = getString(Res.string.tasks_update_error),
                      type = MessageType.ERROR,
                    )
                  )
                }
              }
            }
          }

          is Event.UncheckCancelled -> {
            taskToUncheck = null
          }

          is Event.TaskDeleted -> {
            coroutineScope.launch {
              tasksService.deleteTask(event.task.id).fold(
                onSuccess = {
                  messageDispatcher.sendMessage(
                    Message(
                      content = getString(Res.string.tasks_task_removed),
                      type = MessageType.SUCCESS,
                    )
                  )
                },
                onFailure = {
                  messageDispatcher.sendMessage(
                    Message(
                      content = getString(Res.string.tasks_delete_error),
                      type = MessageType.ERROR,
                    )
                  )
                }
              )
            }
          }

          is Event.SectionToggled -> {
            val currentSections = expandedSections.orEmpty()

            expandedSections = if (event.timeOfDay in currentSections) {
              currentSections - event.timeOfDay
            } else {
              currentSections + event.timeOfDay
            }
          }
        }
      }
    )
  }

  private fun getInitialExpandedSections(
    currentHour: Int,
    currentTasks: Map<TimeOfDay, List<Task>>
  ): Set<TimeOfDay> {
    val timeOfDay = when (currentHour) {
      in 6..11 -> TimeOfDay.Morning
      in 12..17 -> TimeOfDay.Midday
      else -> TimeOfDay.Evening
    }
    val hasPendingTasks = currentTasks[timeOfDay].orEmpty().fastAny { it.completed.not() }

    return if (hasPendingTasks) {
      setOf(timeOfDay)
    } else {
      emptySet()
    }
  }
}
