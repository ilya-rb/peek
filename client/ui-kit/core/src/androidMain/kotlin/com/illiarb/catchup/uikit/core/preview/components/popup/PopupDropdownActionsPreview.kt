package com.illiarb.catchup.uikit.core.preview.components.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.popup.OpenInBrowserAction
import com.illiarb.catchup.uikit.core.components.popup.ShareAction
import com.illiarb.catchup.uikit.core.components.popup.SummarizeAction
import com.illiarb.catchup.uikit.core.preview.components.PreviewTheme

@Composable
internal fun PopupDropdownActionsContent(darkMode: Boolean) {
  PreviewTheme(
    darkMode = darkMode,
    fullscreen = true,
    innerPadding = false,
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
      DropdownMenu(
        expanded = true,
        onDismissRequest = {},
      ) {
        SummarizeAction(onClick = {})
        OpenInBrowserAction(onClick = {})
        ShareAction(onClick = {})
      }
    }
  }
}

@Preview
@Composable
internal fun PopupDropdownActionsPreviewLight() {
  PopupDropdownActionsContent(darkMode = false)
}

@Preview
@Composable
internal fun PopupDropdownActionsPreviewDark() {
  PopupDropdownActionsContent(darkMode = true)
}

