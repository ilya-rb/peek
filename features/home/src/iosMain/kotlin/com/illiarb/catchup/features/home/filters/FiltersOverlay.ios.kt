package com.illiarb.catchup.features.home.filters

import com.illiarb.catchup.service.domain.Tag
import com.slack.circuit.overlay.OverlayHost

actual suspend fun OverlayHost.showFiltersOverlay(model: FiltersOverlayModel): Set<Tag> = model.selectedTags