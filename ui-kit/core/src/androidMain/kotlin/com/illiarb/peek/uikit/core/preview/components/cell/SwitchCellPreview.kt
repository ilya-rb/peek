package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.illiarb.peek.uikit.core.components.cell.SwitchCell
import com.illiarb.peek.uikit.core.model.VectorIcon
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun SwitchCellPreviewLight(
  @PreviewParameter(SwitchCellPreviewProvider::class)
  data: SwitchCellPreviewData
) {
  PreviewTheme(darkMode = false) {
    SwitchCell(
      text = data.text,
      subtitle = data.subtitle,
      startIcon = data.startIcon,
      switchChecked = data.checked,
      onChecked = {}
    )
  }
}

@Composable
@Preview
internal fun SwitchCellPreviewDark(
  @PreviewParameter(SwitchCellPreviewProvider::class)
  data: SwitchCellPreviewData
) {
  PreviewTheme(darkMode = true) {
    SwitchCell(
      text = data.text,
      subtitle = data.subtitle,
      startIcon = data.startIcon,
      switchChecked = data.checked,
      onChecked = {}
    )
  }
}

internal data class SwitchCellPreviewData(
  val text: String,
  val subtitle: String?,
  val startIcon: VectorIcon?,
  val checked: Boolean
)

internal class SwitchCellPreviewProvider : PreviewParameterProvider<SwitchCellPreviewData> {

  override val values = sequenceOf(
    SwitchCellPreviewData(
      text = "Switch title",
      subtitle = "Switch subtitle",
      startIcon = VectorIcon(
        imageVector = Icons.Filled.Check,
        contentDescription = "Check icon",
      ),
      checked = true,
    ),
    SwitchCellPreviewData(
      text = "Switch without icon",
      subtitle = null,
      startIcon = null,
      checked = false,
    ),
  )
}
