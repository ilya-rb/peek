package com.illiarb.catchup.summarizer.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator

public expect suspend fun OverlayHost.showSummaryOverlay(
  screen: SummaryScreen
): SummaryScreen.Result

@Composable
internal fun SummaryOverlay(
  screen: SummaryScreen,
  navigator: OverlayNavigator<SummaryScreen.Result>,
) {
  CircuitContent(
    screen = screen,
    onNavEvent = { event ->
      if (event is NavEvent.Pop) {
        navigator.finish(event.result as SummaryScreen.Result)
      }
    }
  )
}