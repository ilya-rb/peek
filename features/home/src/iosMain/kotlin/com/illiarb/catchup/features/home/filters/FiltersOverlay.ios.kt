package com.illiarb.catchup.features.home.filters

import androidx.compose.ui.graphics.Color
import com.slack.circuit.overlay.OverlayHost

internal actual suspend fun OverlayHost.showFiltersOverlay(
  model: FiltersContract.Model,
  containerColor: Color,
): FiltersContract.Result = FiltersContract.Result.Cancel