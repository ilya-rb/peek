package com.illiarb.catchup.features.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.appinfo.DebugConfig
import com.illiarb.catchup.features.settings.SettingsScreen.Event
import com.illiarb.catchup.uikit.core.components.cell.RowCell
import com.illiarb.catchup.uikit.core.components.cell.SwitchCell
import com.illiarb.catchup.uikit.core.model.VectorIcon
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_icon_appearance
import com.illiarb.catchup.uikit.resources.acsb_navigation_back
import com.illiarb.catchup.uikit.resources.settings_appearance_title
import com.illiarb.catchup.uikit.resources.settings_dark_theme_title
import com.illiarb.catchup.uikit.resources.settings_dynamic_colors_subtitle
import com.illiarb.catchup.uikit.resources.settings_dynamic_colors_title
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
        ui<SettingsScreen.State> { state, _ ->
          SettingsScreen(state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(state: SettingsScreen.State) {
  val events = state.events

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(stringResource(Res.string.settings_screen_title))
        },
        navigationIcon = {
          IconButton(onClick = { events.invoke(Event.NavigationIconClick) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      Column {
        SettingsHeader(
          text = stringResource(Res.string.settings_appearance_title),
          icon = VectorIcon(
            imageVector = Icons.Filled.Palette,
            contentDescription = stringResource(Res.string.acsb_icon_appearance),
          ),
        )
        SwitchCell(
          switchChecked = state.dynamicColorsEnabled,
          text = stringResource(Res.string.settings_dynamic_colors_title),
          subtitle = stringResource(Res.string.settings_dynamic_colors_subtitle),
          onChecked = { checked ->
            events.invoke(Event.MaterialColorsToggleChecked(checked))
          }
        )
        SwitchCell(
          switchChecked = state.darkThemeEnabled,
          text = stringResource(Res.string.settings_dark_theme_title),
          subtitle = null,
          onChecked = { checked ->
            events.invoke(Event.DarkThemeEnabledChecked(checked))
          }
        )
        if (state.debugSettings != null) {
          SettingsHeader(
            modifier = Modifier.padding(top = 16.dp),
            text = "Debug",
            icon = VectorIcon(
              imageVector = Icons.Filled.BugReport,
              contentDescription = "Debug",
            )
          )
          DebugSettings(
            settings = state.debugSettings,
            onNetworkDelayChanged = { events.invoke(Event.NetworkDelayChanged(it)) }
          )
        }
      }
    }
  }
}

@Composable
private fun SettingsHeader(
  modifier: Modifier = Modifier,
  text: String,
  icon: VectorIcon,
) {
  RowCell(
    modifier = modifier.padding(bottom = 8.dp),
    text = text,
    startIcon = icon.imageVector,
    startIconContentDescription = icon.contentDescription,
  )
  HorizontalDivider()
}

@Composable
private fun DebugSettings(
  modifier: Modifier = Modifier,
  settings: DebugConfig,
  onNetworkDelayChanged: (Boolean) -> Unit,
) {
  SwitchCell(
    modifier = modifier,
    switchChecked = settings.networkDelayEnabled,
    text = "Network request delay",
    subtitle = "Delay each network request by 3 sec",
    onChecked = onNetworkDelayChanged,
  )
}

