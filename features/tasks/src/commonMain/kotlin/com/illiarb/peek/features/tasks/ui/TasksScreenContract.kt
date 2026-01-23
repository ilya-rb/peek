package com.illiarb.peek.features.tasks.ui

import androidx.compose.runtime.Immutable
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.TasksScreen
import com.illiarb.peek.features.tasks.TasksService
import com.illiarb.peek.features.tasks.domain.HabitStatistics
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.State
import com.illiarb.peek.uikit.messages.MessageDispatcher
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.datetime.LocalDate

internal interface TasksScreenContract {

  @Immutable
  data class State(
    val tasks: Async<ImmutableMap<TimeOfDay, List<Task>>>,
    val statistics: Async<HabitStatistics>,
    val showAddTaskSheet: Boolean,
    val expandedSections: Set<TimeOfDay>,
    val selectedDate: LocalDate,
    val today: LocalDate,
    val taskToUncheck: Task? = null,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object NavigateBack : Event
    data object AddTaskClicked : Event
    data object AddTaskDismissed : Event
    data object ErrorRetryClicked : Event
    data object PreviousDayClicked : Event
    data object NextDayClicked : Event
    data class TaskToggled(val task: Task) : Event
    data class TaskDeleted(val taskId: String) : Event
    data class SectionToggled(val timeOfDay: TimeOfDay) : Event
    data class AddTaskSubmitted(
      val title: String,
      val isHabit: Boolean,
      val timeOfDay: TimeOfDay,
      val dismissSheet: Boolean,
    ) : Event
    data class UncheckConfirmationRequested(val task: Task) : Event
    data object UncheckConfirmed : Event
    data object UncheckCancelled : Event
  }
}

public interface TasksScreenComponent {

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is TasksScreen) {
        ui<State> { state, modifier -> TasksScreen(state, modifier) }
      } else {
        null
      }
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class PresenterFactory(
    private val tasksService: TasksService,
    private val messageDispatcher: MessageDispatcher,
  ) : Presenter.Factory {

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is TasksScreen) {
        TasksScreenPresenter(navigator, tasksService, messageDispatcher)
      } else {
        null
      }
    }
  }
}
