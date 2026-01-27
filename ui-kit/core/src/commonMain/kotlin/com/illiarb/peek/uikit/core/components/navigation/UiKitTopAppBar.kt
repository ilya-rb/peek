package com.illiarb.peek.uikit.core.components.navigation

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_navigation_back
import org.jetbrains.compose.resources.stringResource

@Immutable
public data class ProgressModel(
  @FloatRange(from = 0.0, to = 1.0)
  val progress: () -> Float,
)

public sealed interface UiKitTopAppBarStyle {
  public data object Default : UiKitTopAppBarStyle
  public data object Centered : UiKitTopAppBarStyle
}

public sealed interface NavigationButton {
  public data object None : NavigationButton
  public data object Back : NavigationButton
  public data object Cross : NavigationButton
}

@Composable
public fun UiKitTopAppBar(
  title: @Composable () -> Unit,
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  navigationButton: NavigationButton = NavigationButton.Back,
  onNavigationButtonClick: () -> Unit = {},
  progress: ProgressModel? = null,
  scrollBehavior: TopAppBarScrollBehavior? = null,
  style: UiKitTopAppBarStyle = UiKitTopAppBarStyle.Default,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
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
    val icon = when (navigationButton) {
      NavigationButton.Back -> {
        VectorIcon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(Res.string.acsb_navigation_back),
        )
      }

      NavigationButton.Cross -> {
        VectorIcon(
          imageVector = Icons.Filled.Close,
          contentDescription = stringResource(Res.string.acsb_navigation_back),
        )
      }

      NavigationButton.None -> null
    }

    if (icon != null) {
      UiKitBackButton(
        icon = icon,
        onClick = onNavigationButtonClick,
        modifier = Modifier.padding(horizontal = 8.dp),
      )
    }
  }

  when (style) {
    UiKitTopAppBarStyle.Centered -> {
      CenterAlignedTopAppBar(
        title = title,
        modifier = modifier,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
        actions = actions,
        navigationIcon = navigationIcon
      )
    }

    UiKitTopAppBarStyle.Default -> {
      TopAppBar(
        title = title,
        windowInsets = windowInsets,
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
