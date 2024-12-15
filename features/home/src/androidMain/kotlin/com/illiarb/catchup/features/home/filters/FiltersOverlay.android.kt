package com.illiarb.catchup.features.home.filters

import androidx.compose.material3.ExperimentalMaterial3Api
import com.illiarb.catchup.uikit.core.overlay.BottomSheetOverlay
import com.slack.circuit.overlay.OverlayHost

@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun OverlayHost.showFiltersOverlay(model: FiltersOverlayModel): FiltersOverlayResult {
  return show(
    BottomSheetOverlay<FiltersOverlayModel, FiltersOverlayResult>(
      model = model,
      skipPartiallyExpandedState = true,
      onDismiss = { FiltersOverlayResult.Cancel },
    ) { _, navigator -> FiltersOverlay(model, navigator) }
  )
}