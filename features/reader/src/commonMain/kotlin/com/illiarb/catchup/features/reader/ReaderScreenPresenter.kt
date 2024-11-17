package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import me.tatarka.inject.annotations.Inject

class ReaderScreenPresenter : Presenter<ReaderScreenContract.State> {

  @Composable
  override fun present(): ReaderScreenContract.State {
    return ReaderScreenContract.State
  }

  @Inject
  class Factory : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        is ReaderScreenContract.ReaderScreen -> ReaderScreenPresenter()
        else -> null
      }
    }
  }
}