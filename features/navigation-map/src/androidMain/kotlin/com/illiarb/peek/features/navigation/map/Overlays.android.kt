package com.illiarb.peek.features.navigation.map

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.overlays.BottomSheetOverlay

@OptIn(ExperimentalMaterial3Api::class)
public actual suspend fun <S : Screen, R : Any> OverlayHost.showScreenOverlay(
  screen: S,
  onDismiss: () -> R,
): R {
  return show(
    BottomSheetOverlay(
      model = screen,
      skipPartiallyExpandedState = false,
      onDismiss = onDismiss,
    ) { _, navigator ->
      OverlayUi(screen, navigator)
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
public actual suspend fun <I : Any, R : Any> OverlayHost.showOverlay(
  input: I,
  content: @Composable (I, OverlayNavigator<R>) -> Unit,
  onDismiss: () -> R,
): R {
  return show(
    BottomSheetOverlay(
      model = input,
      // sheetContainerColor = input.containerColor,
      skipPartiallyExpandedState = true,
      onDismiss = {
        onDismiss.invoke()
      },
    ) { input, navigator ->
      content.invoke(input, navigator)
    }
  )
}
