package com.illiarb.catchup.features.home.overlay

import com.slack.circuit.overlay.OverlayHost

internal actual suspend fun OverlayHost.showTagFilterOverlay(input: TagFilterContract.Input): TagFilterContract.Output {
  return TagFilterContract.Output.Cancel
}