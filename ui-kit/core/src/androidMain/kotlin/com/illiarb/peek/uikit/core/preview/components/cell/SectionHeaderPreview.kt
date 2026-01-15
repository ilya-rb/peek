package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.components.cell.SectionHeader
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun SectionHeaderWithIconPreviewLight() {
  PreviewTheme(darkMode = false) {
    SectionHeader(
      title = "Morning",
      icon = Icons.Filled.WbSunny,
      iconContentDescription = "Morning icon",
    )
  }
}

@Composable
@Preview
internal fun SectionHeaderWithIconPreviewDark() {
  PreviewTheme(darkMode = true) {
    SectionHeader(
      title = "Morning",
      icon = Icons.Filled.WbSunny,
      iconContentDescription = "Morning icon",
    )
  }
}

@Composable
@Preview
internal fun SectionHeaderWithoutIconPreviewLight() {
  PreviewTheme(darkMode = false) {
    SectionHeader(
      title = "Section Title",
    )
  }
}

@Composable
@Preview
internal fun SectionHeaderWithoutIconPreviewDark() {
  PreviewTheme(darkMode = true) {
    SectionHeader(
      title = "Section Title",
    )
  }
}
