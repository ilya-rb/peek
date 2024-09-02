package com.illiarb.catchup

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import coil3.compose.setSingletonImageLoaderFactory
import com.illiarb.catchup.core.arch.OpenUrlScreen
import com.illiarb.catchup.di.AndroidUiComponent
import com.illiarb.catchup.di.create
import com.illiarb.catchup.features.home.HomeScreenContract
import com.illiarb.catchup.uikit.core.theme.UiKitTheme
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuitx.android.AndroidScreen
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))

    super.onCreate(savedInstanceState)

    val appComponent = applicationContext.appComponent()
    val activityComponent = AndroidUiComponent::class.create(appComponent, activity = this)

    setContent {
      val backStack = rememberSaveableBackStack(root = HomeScreenContract.HomeScreen)
      val navigator = rememberAndroidScreenAwareNavigator(
        delegate = rememberCircuitNavigator(backStack) {},
        starter = ::navigateTo,
      )

      setSingletonImageLoaderFactory { appComponent.imageLoader }

      CircuitCompositionLocals(activityComponent.circuit) {
        UiKitTheme(useDynamicColors = false) {
          NavigableCircuitContent(
            navigator = navigator,
            backStack = backStack,
          )
        }
      }
    }
  }

  private fun navigateTo(screen: AndroidScreen): Boolean {
    return when (screen) {
      is OpenUrlScreen -> openChromeCustomTab(screen.url)
      else -> false
    }
  }

  private fun openChromeCustomTab(url: String): Boolean {
    val scheme = CustomTabColorSchemeParams.Builder().setToolbarColor(0x000000).build()

    CustomTabsIntent.Builder()
      .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, scheme)
      .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, scheme)
      .setShowTitle(true)
      .build()
      .launchUrl(this, Uri.parse(url))

    return true
  }
}
