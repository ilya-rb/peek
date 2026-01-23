package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitShapes
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_error
import com.illiarb.peek.uikit.resources.common_error_default_action
import com.illiarb.peek.uikit.resources.common_error_default_title
import org.jetbrains.compose.resources.stringResource

@Composable
public fun EmptyState(
  title: String,
  modifier: Modifier = Modifier,
  image: VectorIcon? = null,
  buttonText: String? = null,
  buttonIcon: VectorIcon? = null,
  onButtonClick: () -> Unit = {},
) {
  EmptyStateInternal(title, modifier, image, buttonText, buttonIcon, onButtonClick = onButtonClick)
}

@Composable
public fun ErrorEmptyState(
  title: String = stringResource(Res.string.common_error_default_title),
  modifier: Modifier = Modifier,
  image: VectorIcon? = VectorIcon(
    imageVector = Icons.Outlined.Error,
    contentDescription = stringResource(Res.string.acsb_icon_error),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
  ),
  buttonText: String = stringResource(Res.string.common_error_default_action),
  buttonIcon: VectorIcon? = null,
  onButtonClick: () -> Unit = {},
) {
  EmptyStateInternal(
    title,
    modifier,
    image,
    buttonText,
    buttonIcon,
    MaterialTheme.colorScheme.error,
    ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.error,
      contentColor = MaterialTheme.colorScheme.onError,
    ),
    onButtonClick
  )
}

@Composable
internal fun EmptyStateInternal(
  title: String,
  modifier: Modifier = Modifier,
  image: VectorIcon? = null,
  buttonText: String? = null,
  buttonIcon: VectorIcon? = null,
  iconsColor: Color = MaterialTheme.colorScheme.onSurface,
  buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
  onButtonClick: () -> Unit = {},
) {
  Surface(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
    shape = UiKitShapes.large,
    color = MaterialTheme.colorScheme.surfaceContainer,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      if (image != null) {
        Image(
          modifier = Modifier.size(120.dp).padding(top = 16.dp),
          imageVector = image.imageVector,
          contentDescription = image.contentDescription,
          colorFilter = image.colorFilter ?: ColorFilter.tint(iconsColor),
        )
      }

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = Modifier.padding(
          top = 16.dp,
          bottom = if (buttonText == null) 16.dp else 0.dp,
        ),
      )

      if (buttonText != null) {
        Button(
          modifier = Modifier.padding(vertical = 16.dp),
          onClick = onButtonClick,
          colors = buttonColors,
        ) {
          if (buttonIcon != null) {
            Icon(
              modifier = Modifier.padding(end = 8.dp),
              imageVector = buttonIcon.imageVector,
              contentDescription = buttonIcon.contentDescription,
            )
          }
          Text(text = buttonText)
        }
      }
    }
  }
}

@Preview
@Composable
private fun EmptyStatePreview() {
  PreviewTheme(darkMode = false) {
    EmptyState(title = "No items found")
  }
}

@Preview
@Composable
private fun EmptyStatePreviewDark() {
  PreviewTheme(darkMode = true) {
    EmptyState(title = "No items found")
  }
}

@Preview
@Composable
private fun EmptyStateWithImagePreview() {
  PreviewTheme(darkMode = false) {
    EmptyState(
      title = "No items found",
      image = VectorIcon(
        imageVector = Icons.Outlined.Error,
        contentDescription = "Empty state icon"
      )
    )
  }
}

@Preview
@Composable
private fun EmptyStateWithImagePreviewDark() {
  PreviewTheme(darkMode = true) {
    EmptyState(
      title = "No items found",
      image = VectorIcon(
        imageVector = Icons.Outlined.Error,
        contentDescription = "Empty state icon"
      )
    )
  }
}

@Preview
@Composable
private fun EmptyStateWithButtonPreview() {
  PreviewTheme(darkMode = false) {
    EmptyState(
      title = "No items found",
      buttonText = "Retry",
      onButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun EmptyStateWithButtonPreviewDark() {
  PreviewTheme(darkMode = true) {
    EmptyState(
      title = "No items found",
      buttonText = "Retry",
      onButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun EmptyStateWithImageAndButtonPreview() {
  PreviewTheme(darkMode = false) {
    EmptyState(
      title = "No items found",
      image = VectorIcon(
        imageVector = Icons.Outlined.Error,
        contentDescription = "Empty state icon"
      ),
      buttonText = "Retry",
      onButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun EmptyStateWithImageAndButtonPreviewDark() {
  PreviewTheme(darkMode = true) {
    EmptyState(
      title = "No items found",
      image = VectorIcon(
        imageVector = Icons.Outlined.Error,
        contentDescription = "Empty state icon"
      ),
      buttonText = "Retry",
      onButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun ErrorEmptyStatePreview() {
  PreviewTheme(darkMode = false) {
    ErrorEmptyState(onButtonClick = {})
  }
}

@Preview
@Composable
private fun ErrorEmptyStatePreviewDark() {
  PreviewTheme(darkMode = true) {
    ErrorEmptyState(onButtonClick = {})
  }
}
