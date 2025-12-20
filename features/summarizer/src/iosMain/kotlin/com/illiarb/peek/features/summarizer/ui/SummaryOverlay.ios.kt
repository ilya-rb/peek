package com.illiarb.peek.features.summarizer.ui

import com.illiarb.peek.features.summarizer.ui.SummaryScreen
import com.slack.circuit.overlay.OverlayHost

public actual suspend fun OverlayHost.showSummaryOverlay(
    screen: SummaryScreen,
): SummaryScreen.Result {
  return SummaryScreen.Result.Close
}