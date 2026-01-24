package com.illiarb.peek.uikit.core.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun UiKitTopAppBarTitle(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      text = title,
      style = if (subtitle == null) {
        MaterialTheme.typography.titleLarge
      } else {
        MaterialTheme.typography.bodyLarge
      },
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    if (subtitle != null) {
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Preview
@Composable
private fun UiKitTopAppBarTitlePreview() {
  PreviewTheme(darkMode = false) {
    UiKitTopAppBarTitle(title = "Screen Title")
  }
}

@Preview
@Composable
private fun UiKitTopAppBarTitlePreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBarTitle(title = "Screen Title")
  }
}

@Preview
@Composable
private fun UiKitTopAppBarTitleWithSubtitlePreview() {
  PreviewTheme(darkMode = false) {
    UiKitTopAppBarTitle(
      title = "Screen Title",
      subtitle = "Subtitle text",
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarTitleWithSubtitlePreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBarTitle(
      title = "Screen Title",
      subtitle = "Subtitle text",
    )
  }
}
