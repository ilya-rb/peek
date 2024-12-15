package com.illiarb.catchup.features.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.settings_screen_title
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class SettingsScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is SettingsScreen -> {
        ui<SettingsScreenContract.State> { state, modifier ->
          SettingsScreen(modifier, state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(modifier: Modifier, state: SettingsScreenContract.State) {
  val events = state.events

  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(Res.string.settings_screen_title))
        },
      )
    }
  ) { innerPadding ->

  }
}