package com.illiarb.peek.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.appinfo.DebugConfig
import com.illiarb.peek.features.settings.SettingsScreenContract.Event
import com.illiarb.peek.uikit.core.components.cell.ListHeader
import com.illiarb.peek.uikit.core.components.cell.RowCell
import com.illiarb.peek.uikit.core.components.cell.SwitchCell
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBar
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_appearance
import com.illiarb.peek.uikit.resources.acsb_icon_debug
import com.illiarb.peek.uikit.resources.settings_appearance_title
import com.illiarb.peek.uikit.resources.settings_article_retention_dialog_title
import com.illiarb.peek.uikit.resources.settings_article_retention_subtitle
import com.illiarb.peek.uikit.resources.settings_article_retention_title
import com.illiarb.peek.uikit.resources.settings_dark_theme_title
import com.illiarb.peek.uikit.resources.settings_data_title
import com.illiarb.peek.uikit.resources.settings_dynamic_colors_subtitle
import com.illiarb.peek.uikit.resources.settings_dynamic_colors_title
import com.illiarb.peek.uikit.resources.settings_screen_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsScreen(
  state: SettingsScreenContract.State,
  modifier: Modifier = Modifier,
) {
  val events = state.events

  Scaffold(
    topBar = {
      UiKitTopAppBar(
        title = {
          Text(stringResource(Res.string.settings_screen_title))
        },
        onNavigationButtonClick = {
          events.invoke(Event.NavigationIconClick)
        },
      )
    }
  ) { innerPadding ->
    Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
      SettingsContent(state)
    }
  }
}

@Composable
private fun SettingsContent(
  state: SettingsScreenContract.State,
) {
  val events = state.events

  if (state.showArticlesRetentionSelector) {
    ArticlesRetentionSelector(
      title = stringResource(Res.string.settings_article_retention_dialog_title),
      currentValue = state.articleRetentionDays,
      options = state.articleRetentionDaysOptions,
      onDismiss = { events(Event.ArticlesRetentionSelectorDismissed) },
      onSelected = { events(Event.ArticleRetentionDaysChanged(it)) }
    )
  }

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
  SettingsHeader(
    modifier = Modifier.padding(top = 16.dp),
    text = stringResource(Res.string.settings_data_title),
    icon = VectorIcon(
      imageVector = Icons.Filled.Dataset,
      contentDescription = stringResource(Res.string.settings_data_title),
    ),
  )
  RowCell(
    title = stringResource(Res.string.settings_article_retention_title),
    subtitle = stringResource(
      Res.string.settings_article_retention_subtitle,
      state.articleRetentionDays
    ),
    modifier = Modifier.clickable {
      events(Event.ArticlesRetentionSelectorClicked)
    },
  )
  if (state.debugSettings != null) {
    SettingsHeader(
      modifier = Modifier.padding(top = 16.dp),
      text = "Debug",
      icon = VectorIcon(
        imageVector = Icons.Filled.BugReport,
        contentDescription = stringResource(Res.string.acsb_icon_debug),
      )
    )
    DebugSettings(
      settings = state.debugSettings,
      onNetworkDelayChanged = { events.invoke(Event.NetworkDelayChanged(it)) }
    )
  }
}

@Composable
private fun SettingsHeader(
  modifier: Modifier = Modifier,
  text: String,
  icon: VectorIcon,
) {
  ListHeader(
    modifier = modifier.padding(bottom = 8.dp),
    title = text,
    style = MaterialTheme.typography.bodyLarge,
    icon = VectorIcon(
      icon.imageVector,
      icon.contentDescription
    ),
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

