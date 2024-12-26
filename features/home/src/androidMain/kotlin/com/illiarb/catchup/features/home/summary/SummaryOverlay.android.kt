package com.illiarb.catchup.features.home.summary

import androidx.compose.material3.ExperimentalMaterial3Api
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun OverlayHost.showSummaryOverlay(model: SummaryOverlayContract.Model) {
  return show(
    BottomSheetOverlay(
      model = model,
      skipPartiallyExpandedState = true,
      onDismiss = {},
    ) { _, navigator ->
      SummaryOverlay(model, navigator)
    }
  )
}