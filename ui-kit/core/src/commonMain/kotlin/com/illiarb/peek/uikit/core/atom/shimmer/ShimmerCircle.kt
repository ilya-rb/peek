package com.illiarb.peek.uikit.core.atom.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun ShimmerCircle(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(ShimmerSpec.color)
  )
}

@Preview
@Composable
private fun ShimmerCirclePreview() {
  PreviewTheme(darkMode = false) {
    ShimmerCircle(modifier = Modifier.size(48.dp))
  }
}

@Preview
@Composable
private fun ShimmerCirclePreviewDark() {
  PreviewTheme(darkMode = true) {
    ShimmerCircle(modifier = Modifier.size(48.dp))
  }
}
