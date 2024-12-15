package com.illiarb.catchup.features.home.debug

import androidx.compose.material3.ExperimentalMaterial3Api
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun OverlayHost.showDebugOverlay() {
  return show(
    BottomSheetOverlay(
      model = Unit,
      skipPartiallyExpandedState = true,
      onDismiss = {}
    ) { _, _ ->
      DebugOverlay()
    }
  )
}
