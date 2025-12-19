package com.illiarb.peek.uikit.core.preview.components.cell

import android.R
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.illiarb.peek.uikit.core.components.cell.EmptyState
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
      onButtonClick = { },
      image = { modifier ->
        Image(
          painter = painterResource(id = R.drawable.ic_dialog_dialer),
          contentDescription = "Icon",
          modifier = modifier,
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )
      }
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
      onButtonClick = { },
      image = { modifier ->
        Image(
          painter = painterResource(id = R.drawable.ic_dialog_dialer),
          contentDescription = "Icon",
          modifier = modifier,
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )
      }
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