package com.illiarb.catchup.features.home.summary

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun OverlayHost.showSummaryOverlay(
  model: SummaryOverlayContract.Model,
  containerColor: Color,
): SummaryOverlayContract.Result {
  return show(
    BottomSheetOverlay<SummaryOverlayContract.Model, SummaryOverlayContract.Result>(
      model = model,
      skipPartiallyExpandedState = false,
      sheetContainerColor = containerColor,
      onDismiss = { SummaryOverlayContract.Result.Close },
    ) { _, navigator ->
      SummaryOverlay(containerColor, model, navigator)
    }
  )
}