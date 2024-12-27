package com.illiarb.catchup.features.home.summary

import androidx.compose.ui.graphics.Color
import com.slack.circuit.overlay.OverlayHost

internal actual suspend fun OverlayHost.showSummaryOverlay(
  model: SummaryOverlayContract.Model,
  containerColor: Color,
): SummaryOverlayContract.Result {
  return SummaryOverlayContract.Result.Close
}