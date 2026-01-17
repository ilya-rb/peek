package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Filter
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.model.VectorIcon
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Preview(
  name = "Light Mode - Parameterized",
  group = "EmptyStateParameterized"
)
@Composable
internal fun FullscreenStatePreviewParameterizedLight(
  @PreviewParameter(EmptyStatePreviewProvider::class)
  data: EmptyStateData
) {
  PreviewTheme(darkMode = false) {
    EmptyState(
      title = data.title,
      buttonText = if (data.hasButton) data.buttonText else null,
      image = VectorIcon(
        Icons.Filled.Filter,
        contentDescription = "icon"
      )
    )
  }
}

@Preview(
  name = "Dark Mode - Parameterized",
  group = "EmptyStateParameterized",
)
@Composable
internal fun FullscreenStatePreviewParameterizedDark(
  @PreviewParameter(EmptyStatePreviewProvider::class)
  data: EmptyStateData
) {
  PreviewTheme(darkMode = true) {
    EmptyState(
      title = data.title,
      buttonText = if (data.hasButton) data.buttonText else null,
      image = VectorIcon(
        Icons.Filled.Dialpad,
        contentDescription = "icon",
      )
    )
  }
}

internal data class EmptyStateData(
  val title: String,
  val buttonText: String?,
  val hasButton: Boolean
)

internal class EmptyStatePreviewProvider : PreviewParameterProvider<EmptyStateData> {
  override val values = sequenceOf(
    EmptyStateData("Nothing to See Here", "Try Again", true),
    EmptyStateData("Welcome!", null, false),
    EmptyStateData("You are Offline, 2 line text, 2 line text", "Refresh", true),
  )
}
