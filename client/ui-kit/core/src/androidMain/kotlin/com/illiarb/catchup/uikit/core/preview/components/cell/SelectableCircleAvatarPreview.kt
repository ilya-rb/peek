package com.illiarb.catchup.uikit.core.preview.components.cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.cell.SelectableCircleAvatarLoading
import com.illiarb.catchup.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun SelectableCircleAvatarPreviewLight() {
  PreviewTheme(darkMode = false) {
    SelectableCircleAvatarLoading(selected = false)
  }
}

@Composable
@Preview
internal fun SelectableCircleAvatarPreviewDark() {
  PreviewTheme(darkMode = true) {
    SelectableCircleAvatarLoading(selected = true)
  }
}
