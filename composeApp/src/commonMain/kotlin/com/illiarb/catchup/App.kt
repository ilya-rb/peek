package com.illiarb.catchup

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.illiarb.catchup.di.UiComponent
import com.illiarb.catchup.features.home.HomeScreenContract
import com.illiarb.catchup.uikit.core.theme.UiKitTheme
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator

@Composable
fun App(
  uiComponent: UiComponent,
  imageLoader: ImageLoader,
) {
  val backStack = rememberSaveableBackStack(root = HomeScreenContract.HomeScreen)
  val navigator = rememberCircuitNavigator(backStack) {}

  setSingletonImageLoaderFactory { imageLoader }

  CircuitCompositionLocals(uiComponent.circuit) {
    UiKitTheme(useDynamicColors = false) {
      NavigableCircuitContent(
        navigator = navigator,
        backStack = backStack,
      )
    }
  }
}
