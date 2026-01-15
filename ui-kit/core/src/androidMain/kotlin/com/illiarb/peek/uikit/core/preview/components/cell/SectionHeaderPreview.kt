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
  SectionHeaderWithIconPreview(darkMode = false)
}

@Composable
@Preview
internal fun SectionHeaderWithIconPreviewDark() {
  SectionHeaderWithIconPreview(darkMode = true)
}

@Composable
@Preview
internal fun SectionHeaderWithoutIconPreviewLight() {
  SectionHeaderWithoutIconPreview(darkMode = false)
}

@Composable
@Preview
internal fun SectionHeaderWithoutIconPreviewDark() {
  SectionHeaderWithoutIconPreview(darkMode = true)
}

@Composable
private fun SectionHeaderWithIconPreview(darkMode: Boolean) {
  PreviewTheme(darkMode = darkMode) {
    SectionHeader(
      title = "Morning",
      icon = Icons.Filled.WbSunny,
      iconContentDescription = "Morning icon",
    )
  }
}

@Composable
private fun SectionHeaderWithoutIconPreview(darkMode: Boolean) {
  PreviewTheme(darkMode = darkMode) {
    SectionHeader(
      title = "Section Title",
    )
  }
}
