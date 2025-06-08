package com.illiarb.catchup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil3.compose.setSingletonImageLoaderFactory
import com.illiarb.catchup.core.arch.OpenUrlScreen
import com.illiarb.catchup.core.arch.ShareScreen
import com.illiarb.catchup.core.arch.message.MessageDispatcher
import com.illiarb.catchup.di.AndroidUiComponent
import com.illiarb.catchup.di.create
import com.illiarb.catchup.features.home.HomeScreen
import com.illiarb.catchup.features.settings.data.SettingsService.SettingType
import com.illiarb.catchup.uikit.core.theme.UiKitTheme
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuitx.android.AndroidScreen
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))

    super.onCreate(savedInstanceState)

    val appComponent = applicationContext.appComponent()
    val activityComponent = AndroidUiComponent.create(appComponent, activity = this)
    val settingsService = appComponent.settingsService

    if (appComponent.appConfiguration.isAndroidQ && isTaskRoot) {
      onBackPressedDispatcher.addCallback {
        // https://twitter.com/Piwai/status/1169274622614704129
        // https://issuetracker.google.com/issues/139738913
        finishAfterTransition()
      }
    }

    lifecycleScope.launch {
      activityComponent.toastMessageDispatcher.messages
        .filterNotNull()
        .collect { showToast(it) }
    }

    setContent {
      val dynamicColorsEnabled by settingsService
        .observeSettingChange(SettingType.DYNAMIC_COLORS)
        .collectAsRetainedState(initial = false)

      val darkThemeEnabled by settingsService
        .observeSettingChange(SettingType.DARK_THEME)
        .collectAsRetainedState(initial = isSystemInDarkTheme())

      setSingletonImageLoaderFactory { appComponent.imageLoader }

      UiKitTheme(
        useDynamicColors = dynamicColorsEnabled,
        useDarkTheme = darkThemeEnabled,
      ) {
        val backStack = rememberSaveableBackStack(root = HomeScreen)
        val navigator = rememberAndroidScreenAwareNavigator(
          delegate = rememberCircuitNavigator(backStack) {},
          starter = ::navigateTo,
        )

        CircuitCompositionLocals(activityComponent.circuit) {
          ContentWithOverlays {
            NavigableCircuitContent(
              navigator = navigator,
              backStack = backStack,
              decoration = GestureNavigationDecoration { navigator.pop() },
            )
          }
        }
      }
    }
  }

  private fun navigateTo(screen: AndroidScreen): Boolean {
    return when (screen) {
      is OpenUrlScreen -> openChromeCustomTab(screen.url)
      is ShareScreen -> openShareScreen(screen.url)
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
      .launchUrl(this, url.toUri())

    return true
  }

  private fun openShareScreen(url: String): Boolean {
    val intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, url)
      type = "text/plain"
    }
    val chooser = Intent.createChooser(intent, null)
    startActivity(chooser)

    return true
  }

  private fun showToast(message: MessageDispatcher.Message) {
    Toast.makeText(this, message.content, Toast.LENGTH_LONG).show()
  }
}
