package com.illiarb.peek.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.features.settings.SettingsScreenContract.Event
import com.illiarb.peek.features.settings.data.SettingsService
import com.illiarb.peek.features.settings.data.SettingsService.Settings
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class SettingsScreenPresenter(
  private val navigator: Navigator,
  private val settingsService: SettingsService,
  private val appConfiguration: AppConfiguration,
) : Presenter<SettingsScreenContract.State> {

  @Composable
  override fun present(): SettingsScreenContract.State {
    val coroutineScope = rememberCoroutineScope()

    val settings by settingsService
      .observeSettings()
      .collectAsRetainedState(initial = Settings.defaults())

    val debugSettings by appConfiguration.debugConfig()
      .collectAsRetainedState(initial = null)

    return SettingsScreenContract.State(
      dynamicColorsEnabled = settings.dynamicColors,
      darkThemeEnabled = settings.darkTheme,
      articleRetentionDays = settings.articlesRetentionDays,
      articleRetentionDaysOptions = settings.articlesRetentionDaysOptions.toImmutableList(),
      debugSettings = debugSettings,
      events = { event ->
        when (event) {
          is Event.NavigationIconClick -> {
            navigator.pop()
          }

          is Event.MaterialColorsToggleChecked -> coroutineScope.launch {
            settingsService.updateSettings(settings.copy(dynamicColors = event.checked))
          }

          is Event.DarkThemeEnabledChecked -> coroutineScope.launch {
            settingsService.updateSettings(settings.copy(darkTheme = event.checked))
          }

          is Event.ArticleRetentionDaysChanged -> coroutineScope.launch {
            settingsService.updateSettings(settings.copy(articlesRetentionDays = event.days))
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
