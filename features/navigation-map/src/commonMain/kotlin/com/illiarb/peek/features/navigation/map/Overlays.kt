package com.illiarb.peek.features.navigation.map

import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator
import com.slack.circuit.runtime.screen.Screen

public expect suspend fun <S : Screen, R : Any> OverlayHost.showScreenOverlay(
  screen: S,
  onDismiss: () -> R,
): R

public expect suspend fun <I : Any, R : Any> OverlayHost.showOverlay(
  input: I,
  content: @Composable (I, OverlayNavigator<R>) -> Unit,
  onDismiss: () -> R,
): R

@Composable
internal fun <S : Screen, R : Any> OverlayUi(
  screen: S,
  navigator: OverlayNavigator<R>,
) {
  CircuitContent(
    screen = screen,
    onNavEvent = { event ->
      if (event is NavEvent.Pop) {
        @Suppress("UNCHECKED_CAST")
        navigator.finish(event.result as R)
      }
    }
  )
}

