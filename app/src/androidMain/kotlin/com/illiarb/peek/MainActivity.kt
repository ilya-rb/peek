package com.illiarb.peek

import android.app.Activity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.arch.ActivityKey
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.features.navigation.map.HomeScreen
import com.illiarb.peek.features.navigation.map.OpenUrlScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.illiarb.peek.features.settings.data.SettingsService
import com.illiarb.peek.features.settings.data.SettingsService.SettingType
import com.illiarb.peek.uikit.core.theme.UiKitTheme
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuitx.android.AndroidScreen
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.filterNotNull

@Inject
@ActivityKey(MainActivity::class)
@ContributesIntoMap(AppScope::class, binding = binding<Activity>())
internal class MainActivity(
  private val settingsService: SettingsService,
  private val imageLoader: ImageLoader,
  private val appConfig: AppConfiguration,
) : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))

    super.onCreate(savedInstanceState)

    val appGraph = applicationContext.appGraph()
    val uiGraph = appGraph.uiGraph.create(this)

    if (appConfig.isAndroidQ && isTaskRoot) {
      onBackPressedDispatcher.addCallback {
        // https://twitter.com/Piwai/status/1169274622614704129
        // https://issuetracker.google.com/issues/139738913
        finishAfterTransition()
      }
    }

    setContent {
      val dynamicColorsEnabled by settingsService
        .observeSettingChange(SettingType.DYNAMIC_COLORS)
        .collectAsRetainedState(initial = false)

      val darkThemeEnabled by settingsService
        .observeSettingChange(SettingType.DARK_THEME)
        .collectAsRetainedState(initial = isSystemInDarkTheme())

      val message by uiGraph.messageProvider.messages
        .collectAsState(null)

      setSingletonImageLoaderFactory { imageLoader }

      UiKitTheme(
        useDynamicColors = dynamicColorsEnabled,
        useDarkTheme = darkThemeEnabled,
      ) {
        val backStack = rememberSaveableBackStack(root = HomeScreen)
        val navigator = rememberAndroidScreenAwareNavigator(
          delegate = rememberCircuitNavigator(backStack) {},
          starter = ::navigateTo,
        )

        message?.let {
          SideEffect {
            showToast(it)
          }
        }

        CircuitCompositionLocals(uiGraph.circuit) {
          ContentWithOverlays {
            NavigableCircuitContent(
              navigator = navigator,
              backStack = backStack,
              decoratorFactory = GestureNavigationDecorationFactory(
                uiGraph.circuit.animatedNavDecoratorFactory,
                navigator::pop,
              ),
            )
          }
        }
      }
    }
  }

  private fun navigateTo(screen: AndroidScreen): Boolean {
    return when (screen) {
      is OpenUrlScreen -> openChromeCustomTab(screen.url.url)
      is ShareScreen -> openShareScreen(screen.url.url)
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
