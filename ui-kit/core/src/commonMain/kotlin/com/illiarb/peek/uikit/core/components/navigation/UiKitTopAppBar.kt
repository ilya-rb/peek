package com.illiarb.peek.uikit.core.components.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun UiKitTopAppBar(
  title: @Composable () -> Unit,
  showNavigationButton: Boolean = true,
  onNavigationButtonClick: () -> Unit = {},
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  modifier: Modifier = Modifier,
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
    title = title,
    modifier = modifier,
    colors = colors,
    scrollBehavior = scrollBehavior,
    actions = actions,
    navigationIcon = {
      if (showNavigationButton) {
        UiKitBackButton(
          onNavigationButtonClick,
          modifier = Modifier.padding(horizontal = 8.dp),
        )
      }
    }
  )
}

@Preview
@Composable
private fun UiKitTopAppBarPreview() {
  PreviewTheme(darkMode = false) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarPreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarWithActionsPreview() {
  PreviewTheme(darkMode = false) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {},
      actions = {
        IconButton(onClick = {}) {
          Icon(Icons.Filled.Settings, contentDescription = null)
        }
      }
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarWithActionsPreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {},
      actions = {
        IconButton(onClick = {}) {
          Icon(Icons.Filled.Settings, contentDescription = null)
        }
      }
    )
  }
}
