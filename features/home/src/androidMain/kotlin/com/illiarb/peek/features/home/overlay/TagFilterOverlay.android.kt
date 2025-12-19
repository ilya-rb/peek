package com.illiarb.peek.features.home.overlay

import androidx.compose.material3.ExperimentalMaterial3Api
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun OverlayHost.showTagFilterOverlay(
  input: TagFilterContract.Input,
): TagFilterContract.Output {
  return show(
    BottomSheetOverlay<TagFilterContract.Input, TagFilterContract.Output>(
      model = input,
      sheetContainerColor = input.containerColor,
      skipPartiallyExpandedState = true,
      onDismiss = {
        TagFilterContract.Output.Cancel
      },
    ) { input, navigator ->
      TagFilterOverlay(input, navigator)
    }
  )
}