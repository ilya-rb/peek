package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject

@Inject
class Factory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is ReaderScreenContract.ReaderScreen -> {
        ui<ReaderScreenContract.State> { state, modifier ->
          ReaderScreen(modifier, state)
        }
      }

      else -> null
    }
  }
}

@Composable
fun ReaderScreen(modifier: Modifier, state: ReaderScreenContract.State) {

}
