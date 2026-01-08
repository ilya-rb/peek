package com.illiarb.peek.features.settings

import androidx.compose.runtime.Stable
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.DebugConfig
import com.illiarb.peek.core.arch.di.UiScope
import com.illiarb.peek.features.navigation.map.SettingsScreen
import com.illiarb.peek.features.settings.data.SettingsService
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList

internal interface SettingsScreenContract {

  @Stable
  data class State(
    val events: (Event) -> Unit,
    val dynamicColorsEnabled: Boolean,
    val darkThemeEnabled: Boolean,
    val articleRetentionDays: Int,
    val articleRetentionDaysOptions: ImmutableList<Int>,
    val debugSettings: DebugConfig?,
  ) : CircuitUiState

  sealed interface Event {
    data object NavigationIconClick : Event
    data class MaterialColorsToggleChecked(val checked: Boolean) : Event
    data class DarkThemeEnabledChecked(val checked: Boolean) : Event
    data class ArticleRetentionDaysChanged(val days: Int) : Event
    data class NetworkDelayChanged(val checked: Boolean) : Event
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is SettingsScreen) {
        ui<State> { state, modifier -> SettingsScreen(state, modifier) }
      } else {
        null
      }
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  class PresenterFactory(
    private val settingsService: SettingsService,
    private val appConfiguration: AppConfiguration,
  ) : Presenter.Factory {

    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is SettingsScreen) {
        SettingsScreenPresenter(navigator, settingsService, appConfiguration)
      } else {
        null
      }
    }
  }
}
