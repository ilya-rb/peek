package com.illiarb.catchup.features.settings

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.appinfo.DebugConfig
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data object SettingsScreen : Screen, CommonParcelable {

  @Stable
  internal data class State(
    val events: (Event) -> Unit,
    val dynamicColorsEnabled: Boolean,
    val darkThemeEnabled: Boolean,
    val debugSettings: DebugConfig?,
  ) : CircuitUiState

  internal sealed interface Event {
    data object NavigationIconClick : Event
    data class MaterialColorsToggleChecked(val checked: Boolean) : Event
    data class DarkThemeEnabledChecked(val checked: Boolean) : Event
    data class NetworkDelayChanged(val checked: Boolean) : Event
  }
}