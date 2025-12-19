package com.illiarb.peek.features.settings

import androidx.compose.runtime.Stable
import com.illiarb.peek.core.appinfo.DebugConfig
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data object SettingsScreen : Screen, CommonParcelable {

  @Stable
  public data class State(
    val events: (Event) -> Unit,
    val dynamicColorsEnabled: Boolean,
    val darkThemeEnabled: Boolean,
    val debugSettings: DebugConfig?,
  ) : CircuitUiState

  public sealed interface Event {
    public data object NavigationIconClick : Event
    public data class MaterialColorsToggleChecked(val checked: Boolean) : Event
    public data class DarkThemeEnabledChecked(val checked: Boolean) : Event
    public data class NetworkDelayChanged(val checked: Boolean) : Event
  }
}