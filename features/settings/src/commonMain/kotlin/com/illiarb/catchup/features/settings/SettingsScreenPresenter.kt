package com.illiarb.catchup.features.settings

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import me.tatarka.inject.annotations.Inject

@Inject
public class SettingsScreenPresenterFactory : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is SettingsScreen -> SettingsScreenPresenter()
      else -> null
    }
  }
}

internal class SettingsScreenPresenter : Presenter<SettingsScreenContract.State> {

  @Composable
  override fun present(): SettingsScreenContract.State {
    return SettingsScreenContract.State {

    }
  }
}