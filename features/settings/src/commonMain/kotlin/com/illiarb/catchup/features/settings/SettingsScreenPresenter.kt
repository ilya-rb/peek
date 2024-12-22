package com.illiarb.catchup.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.illiarb.catchup.core.appinfo.AppConfiguration
import com.illiarb.catchup.features.settings.data.SettingsService
import com.illiarb.catchup.features.settings.data.SettingsService.SettingType.DYNAMIC_COLORS
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

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
) : Presenter<SettingsScreenContract.State> {

  @Composable
  override fun present(): SettingsScreenContract.State {
    val coroutineScope = rememberCoroutineScope()

    val dynamicColorsEnabled by settingsService
      .observeSettingChange(DYNAMIC_COLORS)
      .collectAsRetainedState(initial = false)

    val debugSettings by appConfiguration.debugConfig()
      .collectAsRetainedState(initial = null)

    return SettingsScreenContract.State(
      dynamicColorsEnabled = dynamicColorsEnabled,
      debugSettings = debugSettings,
      events = { event ->
        when (event) {
          is SettingsScreenContract.Event.NavigationIconClick -> navigator.pop()
          is SettingsScreenContract.Event.MaterialColorsToggleChecked -> {
            settingsService.updateSetting(type = DYNAMIC_COLORS, value = event.checked)
          }

          is SettingsScreenContract.Event.NetworkDelayChanged -> {
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