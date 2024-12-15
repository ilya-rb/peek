package com.illiarb.catchup.features.settings

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
public data object SettingsScreen : Screen, CommonParcelable

internal interface SettingsScreenContract {

  @Stable
  data class State(
    val events: (Event) -> Unit,
    val dynamicColorsEnabled: Boolean,
  ) : CircuitUiState

  sealed interface Event {
    data object NavigationIconClick : Event
    data class MaterialColorsToggleChecked(val checked: Boolean) : Event
  }
}