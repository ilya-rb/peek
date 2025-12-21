package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.illiarb.peek.uikit.core.components.cell.RowCell
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

internal data class RowCellPreviewData(
  val text: String,
  val startIcon: ImageVector,
  val startIconContentDescription: String,
  val endActionText: String?,
)

internal class RowCellPreviewProvider : PreviewParameterProvider<RowCellPreviewData> {
  override val values = sequenceOf(
    RowCellPreviewData(
      text = "Row with action",
      startIcon = Icons.Filled.Check,
      startIconContentDescription = "Check icon",
      endActionText = "Action",
    ),
    RowCellPreviewData(
      text = "Row without action",
      startIcon = Icons.Filled.Check,
      startIconContentDescription = "Check icon",
      endActionText = null,
    ),
  )
}

@Composable
@Preview
internal fun RowCellPreviewLight(
  @PreviewParameter(RowCellPreviewProvider::class)
  data: RowCellPreviewData
) {
  PreviewTheme(darkMode = false) {
    RowCell(
      text = data.text,
      startIcon = data.startIcon,
      startIconContentDescription = data.startIconContentDescription,
      endActionText = data.endActionText,
      onEndActionClicked = {},
    )
  }
}

@Composable
@Preview
internal fun RowCellPreviewDark(
  @PreviewParameter(RowCellPreviewProvider::class)
  data: RowCellPreviewData
) {
  PreviewTheme(darkMode = true) {
    RowCell(
      text = data.text,
      startIcon = data.startIcon,
      startIconContentDescription = data.startIconContentDescription,
      endActionText = data.endActionText,
      onEndActionClicked = {},
    )
  }
}
