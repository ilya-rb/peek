package com.illiarb.peek.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.features.settings.SettingsScreen.Event
import com.illiarb.peek.features.settings.data.SettingsService
import com.illiarb.peek.features.settings.data.SettingsService.SettingType.DARK_THEME
import com.illiarb.peek.features.settings.data.SettingsService.SettingType.DYNAMIC_COLORS
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch

@AssistedInject
public class SettingsScreenPresenter(
  @Assisted private val navigator: Navigator,
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

  @AssistedFactory
  @CircuitInject(SettingsScreen::class, UiScope::class)
  public fun interface Factory {
    public fun create(navigator: Navigator): SettingsScreenPresenter
  }
}