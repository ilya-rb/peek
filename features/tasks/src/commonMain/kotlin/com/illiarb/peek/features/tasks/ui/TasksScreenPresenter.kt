package com.illiarb.peek.features.tasks.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter

internal class TasksScreenPresenter(
  private val navigator: Navigator,
) : Presenter<TasksScreenContract.State> {

  @Composable
  override fun present(): TasksScreenContract.State {
    return TasksScreenContract.State(
      eventSink = { event ->
        when (event) {
          is TasksScreenContract.Event.NavigateBack -> {
            navigator.pop()
          }
        }
      }
    )
  }
}
