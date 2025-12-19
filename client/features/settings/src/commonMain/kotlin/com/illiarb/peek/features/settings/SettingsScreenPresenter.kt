package com.illiarb.peek.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.features.settings.SettingsScreen.Event
import com.illiarb.peek.features.settings.data.SettingsService
import com.illiarb.peek.features.settings.data.SettingsService.SettingType.DARK_THEME
import com.illiarb.peek.features.settings.data.SettingsService.SettingType.DYNAMIC_COLORS
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@Inject
public class SettingsScreenPresenterFactory(
  private val settingsService: SettingsService,
  private val appConfiguration: AppConfiguration,
) : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is SettingsScreen -> SettingsScreenPresenter(navigator, settingsService, appConfiguration)
      else -> null
    }
  }
}

internal class SettingsScreenPresenter(
  private val navigator: Navigator,
  private val settingsService: SettingsService,
  private val appConfiguration: AppConfiguration,
) : Presenter<SettingsScreen.State> {

  @Composable
  override fun present(): SettingsScreen.State {
    val coroutineScope = rememberCoroutineScope()

    val dynamicColorsEnabled by settingsService
      .observeSettingChange(DYNAMIC_COLORS)
      .collectAsRetainedState(initial = false)

    val darkThemeEnabled by settingsService
      .observeSettingChange(DARK_THEME)
      .collectAsRetainedState(initial = false)

    val debugSettings by appConfiguration.debugConfig()
      .collectAsRetainedState(initial = null)

    return SettingsScreen.State(
      dynamicColorsEnabled = dynamicColorsEnabled,
      darkThemeEnabled = darkThemeEnabled,
      debugSettings = debugSettings,
      events = { event ->
        when (event) {
          is Event.NavigationIconClick -> {
            navigator.pop()
          }

          is Event.MaterialColorsToggleChecked -> coroutineScope.launch {
            settingsService.updateSetting(DYNAMIC_COLORS, event.checked)
          }

          is Event.DarkThemeEnabledChecked -> coroutineScope.launch {
            settingsService.updateSetting(DARK_THEME, event.checked)
          }

          is Event.NetworkDelayChanged -> {
            val newConfig = debugSettings?.copy(networkDelayEnabled = event.checked)
            if (newConfig != null) {
              coroutineScope.launch {
                appConfiguration.updateDebugConfig(newConfig)
              }
            }
          }
        }
      }
    )
  }
}