package com.illiarb.peek.features.tasks.ui

import androidx.compose.runtime.Immutable
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.features.tasks.ui.TasksScreenContract.State
import com.illiarb.peek.features.navigation.map.TasksScreen
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

internal interface TasksScreenContract {

  @Immutable
  data class State(
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object NavigateBack : Event
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
  public class PresenterFactory : Presenter.Factory {

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is TasksScreen) {
        TasksScreenPresenter(navigator)
      } else {
        null
      }
    }
  }
}
