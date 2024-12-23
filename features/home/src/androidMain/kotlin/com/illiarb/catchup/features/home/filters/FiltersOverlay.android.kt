package com.illiarb.catchup.features.home.filters

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun OverlayHost.showFiltersOverlay(
  model: FiltersContract.Model,
  containerColor: Color,
): FiltersContract.Result {
  return show(
    BottomSheetOverlay<FiltersContract.Model, FiltersContract.Result>(
      model = model,
      sheetContainerColor = containerColor,
      skipPartiallyExpandedState = true,
      onDismiss = { FiltersContract.Result.Cancel },
    ) { _, navigator -> FiltersOverlay(model, navigator) }
  )
}