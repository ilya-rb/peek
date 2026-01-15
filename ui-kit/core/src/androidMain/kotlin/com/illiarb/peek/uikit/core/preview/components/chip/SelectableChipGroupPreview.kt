package com.illiarb.peek.uikit.core.preview.components.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.components.chip.SelectableChipGroup
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview
internal fun SelectableChipGroupPreviewLight() {
  SelectableChipGroupPreview(darkMode = false)
}

@Composable
@Preview
internal fun SelectableChipGroupPreviewDark() {
  SelectableChipGroupPreview(darkMode = true)
}

@Composable
@Preview
internal fun SelectableChipGroupMultiRowPreviewLight() {
  SelectableChipGroupMultiRowPreview(darkMode = false)
}

@Composable
@Preview
internal fun SelectableChipGroupMultiRowPreviewDark() {
  SelectableChipGroupMultiRowPreview(darkMode = true)
}

@Composable
private fun SelectableChipGroupPreview(darkMode: Boolean) {
  PreviewTheme(darkMode = darkMode) {
    SelectableChipGroup(
      options = listOf("Anytime", "Morning", "Midday", "Evening").toImmutableList(),
      selectedOption = "Anytime",
      onOptionSelected = {},
      labelProvider = { it },
    )
  }
}

@Composable
private fun SelectableChipGroupMultiRowPreview(darkMode: Boolean) {
  PreviewTheme(darkMode = darkMode) {
    SelectableChipGroup(
      options = listOf(
        "Option 1",
        "Option 2",
        "Option 3",
        "Option 4",
        "Option 5",
        "Option 6"
      ).toImmutableList(),
      selectedOption = "Option 3",
      onOptionSelected = {},
      labelProvider = { it },
    )
  }
}
