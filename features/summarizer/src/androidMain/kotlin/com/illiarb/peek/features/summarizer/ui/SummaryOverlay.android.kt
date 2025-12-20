package com.illiarb.peek.features.summarizer.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
public actual suspend fun OverlayHost.showSummaryOverlay(
  screen: SummaryScreen,
): SummaryScreen.Result {
  return show(
    BottomSheetOverlay<SummaryScreen, SummaryScreen.Result>(
      model = screen,
      skipPartiallyExpandedState = false,
      onDismiss = { SummaryScreen.Result.Close },
    ) { _, navigator ->
      SummaryOverlay(screen, navigator)
    }
  )
}