package com.illiarb.catchup.features.home.filters

import com.illiarb.catchup.service.domain.Tag
import com.slack.circuit.overlay.OverlayHost

actual suspend fun OverlayHost.showFiltersOverlay(model: FiltersModel): Set<Tag> = model.selectedTags