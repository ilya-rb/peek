package com.illiarb.catchup.features.home.filters

import androidx.compose.material3.ExperimentalMaterial3Api
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.uikit.core.overlay.BottomSheetOverlay
import com.slack.circuit.overlay.OverlayHost

@OptIn(ExperimentalMaterial3Api::class)
actual suspend fun OverlayHost.showFiltersOverlay(model: FiltersOverlayModel): Set<Tag> {
  return show(
    BottomSheetOverlay(
      model = model,
      skipPartiallyExpandedState = true,
      onDismiss = { model.selectedTags },
    ) { _, navigator -> FiltersOverlay(model, navigator) }
  )
}