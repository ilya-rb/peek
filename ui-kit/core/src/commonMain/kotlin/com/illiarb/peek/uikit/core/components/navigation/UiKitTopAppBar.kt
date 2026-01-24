package com.illiarb.peek.uikit.core.components.navigation

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Immutable
public data class ProgressModel(
  @FloatRange(from = 0.0, to = 1.0)
  val progress: () -> Float,
)

public sealed interface UiKitTopAppBarStyle {
  public data object Default : UiKitTopAppBarStyle
  public data object Centered : UiKitTopAppBarStyle
}

@Composable
public fun UiKitTopAppBar(
  title: @Composable () -> Unit,
  style: UiKitTopAppBarStyle = UiKitTopAppBarStyle.Default,
  showNavigationButton: Boolean = true,
  onNavigationButtonClick: () -> Unit = {},
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  progress: ProgressModel? = null,
  modifier: Modifier = Modifier,
  actions: @Composable RowScope.() -> Unit = {},
) {
  val modifier = modifier.let {
    if (progress != null) {
      it.withProgressLine(progress.progress)
    } else {
      it
    }
  }
  val navigationIcon = @Composable {
    if (showNavigationButton) {
      UiKitBackButton(
        onNavigationButtonClick,
        modifier = Modifier.padding(horizontal = 8.dp),
      )
    }
  }

  when (style) {
    UiKitTopAppBarStyle.Centered -> {
      CenterAlignedTopAppBar(
        title = title,
        modifier = modifier,
        colors = colors,
        scrollBehavior = scrollBehavior,
        actions = actions,
        navigationIcon = navigationIcon
      )
    }

    UiKitTopAppBarStyle.Default -> {
      TopAppBar(
        title = title,
        modifier = modifier,
        colors = colors,
        scrollBehavior = scrollBehavior,
        actions = actions,
        navigationIcon = navigationIcon
      )
    }
  }
}

@Composable
private fun Modifier.withProgressLine(
  progress: () -> Float,
): Modifier {
  val progressColor = MaterialTheme.colorScheme.primary
  val progressSize = 8.dp.value

  return drawWithContent {
    drawContent()
    drawRect(
      brush = SolidColor(progressColor),
      size = Size(
        height = progressSize,
        width = (progress() * size.width).coerceIn(0f, this.size.width),
      ),
      topLeft = Offset(
        x = 0f,
        y = this.size.height - progressSize,
      )
    )
  }
}


@Preview
@Composable
private fun UiKitTopAppBarPreview() {
  PreviewTheme(darkMode = false) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarPreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {}
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarWithActionsPreview() {
  PreviewTheme(darkMode = false) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {},
      actions = {
        IconButton(onClick = {}) {
          Icon(Icons.Filled.Settings, contentDescription = null)
        }
      }
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarWithActionsPreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {},
      actions = {
        IconButton(onClick = {}) {
          Icon(Icons.Filled.Settings, contentDescription = null)
        }
      }
    )
  }
}

@Preview
@Composable
private fun UiKitTopAppBarWithProgressPreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitTopAppBar(
      title = { Text("Screen Title") },
      onNavigationButtonClick = {},
      progress = ProgressModel(progress = { 0.5f }),
    )
  }
}
