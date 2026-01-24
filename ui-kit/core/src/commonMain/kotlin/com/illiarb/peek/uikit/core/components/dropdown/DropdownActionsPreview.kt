package com.illiarb.peek.uikit.core.components.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Preview
@Composable
private fun PopupDropdownActionsPreview() {
  PreviewTheme(darkMode = false) {
    SummarizeAction(onClick = {})

    OpenInBrowserAction(onClick = {})

    ShareAction(onClick = {})
  }
}

@Preview
@Composable
private fun PopupDropdownActionsPreviewDark() {
  PreviewTheme(darkMode = true) {
    SummarizeAction(onClick = {})

    OpenInBrowserAction(onClick = {})

    ShareAction(onClick = {})
  }
}
