package com.illiarb.peek.uikit.core.components.cell.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.atom.shimmer.ShimmerCircle
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.valentinilk.shimmer.shimmer

@Composable
public fun SelectableCircleAvatarLoading(
  modifier: Modifier = Modifier,
  selected: Boolean,
) {
  val scale = if (selected) 1f else 0.8f

  ShimmerCircle(
    modifier = modifier
      .shimmer()
      .size(48.dp)
      .graphicsLayer(scaleX = scale, scaleY = scale)
  )
}

@Preview
@Composable
private fun SelectableCircleAvatarLoadingFullPreviewLight() {
  PreviewTheme(darkMode = false) {
    SelectableCircleAvatarLoadingFullPreview()
  }
}

@Preview
@Composable
private fun SelectableCircleAvatarLoadingFullPreviewDark() {
  PreviewTheme(darkMode = true) {
    SelectableCircleAvatarLoadingFullPreview()
  }
}

@Composable
private fun SelectableCircleAvatarLoadingFullPreview() {
  SelectableCircleAvatarLoading(selected = true)
}

@Preview
@Composable
private fun SelectableCircleAvatarLoadingStatesPreview() {
  PreviewTheme(darkMode = false) {
    SelectableCircleAvatarLoadingStatesPreviewContent()
  }
}

@Composable
private fun SelectableCircleAvatarLoadingStatesPreviewContent() {
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    SelectableCircleAvatarLoading(selected = false)
    SelectableCircleAvatarLoading(selected = true)
  }
}
