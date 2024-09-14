package com.illiarb.catchup.features.home.debug

import androidx.compose.material3.ExperimentalMaterial3Api
import com.illiarb.catchup.uikit.core.overlay.BottomSheetOverlay
import com.slack.circuit.overlay.OverlayHost

@OptIn(ExperimentalMaterial3Api::class)
actual suspend fun OverlayHost.showDebugOverlay() {
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
