package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.illiarb.peek.uikit.core.components.cell.CheckableListItem
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun CheckableListItemPreviewLight(
  @PreviewParameter(CheckableListItemPreviewProvider::class)
  data: CheckableListItemPreviewData
) {
  CheckableListItemPreview(data = data, darkMode = false)
}

@Composable
@Preview
internal fun CheckableListItemPreviewDark(
  @PreviewParameter(CheckableListItemPreviewProvider::class)
  data: CheckableListItemPreviewData
) {
  CheckableListItemPreview(data = data, darkMode = true)
}

@Composable
private fun CheckableListItemPreview(
  data: CheckableListItemPreviewData,
  darkMode: Boolean,
) {
  PreviewTheme(darkMode = darkMode) {
    CheckableListItem(
      text = data.text,
      checked = data.checked,
      onCheckedChange = {},
      trailingContent = if (data.hasTrailingIcon) {
        { Icon(imageVector = Icons.Filled.Refresh, contentDescription = null) }
      } else {
        null
      },
    )
  }
}

internal data class CheckableListItemPreviewData(
  val text: String,
  val checked: Boolean,
  val hasTrailingIcon: Boolean,
)

internal class CheckableListItemPreviewProvider :
  PreviewParameterProvider<CheckableListItemPreviewData> {

  override val values = sequenceOf(
    CheckableListItemPreviewData(
      text = "Unchecked item",
      checked = false,
      hasTrailingIcon = false,
    ),
    CheckableListItemPreviewData(
      text = "Checked item",
      checked = true,
      hasTrailingIcon = false,
    ),
    CheckableListItemPreviewData(
      text = "Item with trailing icon",
      checked = false,
      hasTrailingIcon = true,
    ),
    CheckableListItemPreviewData(
      text = "Checked item with trailing icon",
      checked = true,
      hasTrailingIcon = true,
    ),
  )
}
