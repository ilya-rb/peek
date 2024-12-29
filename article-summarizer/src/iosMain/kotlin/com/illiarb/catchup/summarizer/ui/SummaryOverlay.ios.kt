package com.illiarb.catchup.summarizer.ui

import com.slack.circuit.overlay.OverlayHost

public actual suspend fun OverlayHost.showSummaryOverlay(
  screen: SummaryScreen,
): SummaryScreen.Result {
  return SummaryScreen.Result.Close
}