package com.illiarb.peek.uikit.core.atom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.model.ButtonModel
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun HorizontalButtons(
  primary: ButtonModel? = null,
  secondary: ButtonModel? = null,
  modifier: Modifier = Modifier,
) {
  check(primary != null || secondary != null) {
    "At least one button should be present"
  }

  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    if (secondary != null) {
      OutlinedButton(
        modifier = Modifier.weight(1f),
        enabled = secondary.enabled,
        content = { Text(secondary.text) },
        onClick = secondary.onClick,
      )
    }
    if (primary != null) {
      Button(
        modifier = Modifier.weight(1f),
        enabled = primary.enabled,
        content = { Text(primary.text) },
        onClick = primary.onClick,
      )
    }
  }
}

@Preview
@Composable
private fun HorizontalButtonsBothPreviewLight() {
  PreviewTheme(darkMode = false) {
    HorizontalButtons(
      primary = ButtonModel(text = "Confirm", onClick = {}),
      secondary = ButtonModel(text = "Cancel", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsBothPreviewDark() {
  PreviewTheme(darkMode = true) {
    HorizontalButtons(
      primary = ButtonModel(text = "Confirm", onClick = {}),
      secondary = ButtonModel(text = "Cancel", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsPrimaryOnlyPreviewLight() {
  PreviewTheme(darkMode = false) {
    HorizontalButtons(
      primary = ButtonModel(text = "Save", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsPrimaryOnlyPreviewDark() {
  PreviewTheme(darkMode = true) {
    HorizontalButtons(
      primary = ButtonModel(text = "Save", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsSecondaryOnlyPreviewLight() {
  PreviewTheme(darkMode = false) {
    HorizontalButtons(
      secondary = ButtonModel(text = "Cancel", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsSecondaryOnlyPreviewDark() {
  PreviewTheme(darkMode = true) {
    HorizontalButtons(
      secondary = ButtonModel(text = "Cancel", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsDisabledPreviewLight() {
  PreviewTheme(darkMode = false) {
    HorizontalButtons(
      primary = ButtonModel(text = "Confirm", enabled = false, onClick = {}),
      secondary = ButtonModel(text = "Cancel", onClick = {}),
    )
  }
}

@Preview
@Composable
private fun HorizontalButtonsDisabledPreviewDark() {
  PreviewTheme(darkMode = true) {
    HorizontalButtons(
      primary = ButtonModel(text = "Confirm", enabled = false, onClick = {}),
      secondary = ButtonModel(text = "Cancel", onClick = {}),
    )
  }
}
