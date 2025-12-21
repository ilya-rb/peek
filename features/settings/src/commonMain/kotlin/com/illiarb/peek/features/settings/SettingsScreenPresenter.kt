package com.illiarb.peek.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.features.settings.SettingsScreenContract.Event
import com.illiarb.peek.features.settings.data.SettingsService
import com.illiarb.peek.features.settings.data.SettingsService.SettingType.DARK_THEME
import com.illiarb.peek.features.settings.data.SettingsService.SettingType.DYNAMIC_COLORS
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch

internal class SettingsScreenPresenter(
  private val navigator: Navigator,
  private val settingsService: SettingsService,
  private val appConfiguration: AppConfiguration,
) : Presenter<SettingsScreenContract.State> {

  @Composable
  override fun present(): SettingsScreenContract.State {
    val coroutineScope = rememberCoroutineScope()

    val dynamicColorsEnabled by settingsService
      .observeSettingChange(DYNAMIC_COLORS)
      .collectAsRetainedState(initial = false)

    val darkThemeEnabled by settingsService
      .observeSettingChange(DARK_THEME)
      .collectAsRetainedState(initial = false)

    val debugSettings by appConfiguration.debugConfig()
      .collectAsRetainedState(initial = null)

    return SettingsScreenContract.State(
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
