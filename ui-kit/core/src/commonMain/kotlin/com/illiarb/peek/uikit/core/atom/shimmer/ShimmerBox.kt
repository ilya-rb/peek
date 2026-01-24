package com.illiarb.peek.uikit.core.atom.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun ShimmerBox(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(ShimmerSpec.color())
  )
}

@Preview
@Composable
private fun ShimmerBoxPreview() {
  PreviewTheme(darkMode = false) {
    ShimmerBox(modifier = Modifier.width(200.dp).height(50.dp))
  }
}

@Preview
@Composable
private fun ShimmerBoxPreviewDark() {
  PreviewTheme(darkMode = true) {
    ShimmerBox(modifier = Modifier.width(200.dp).height(50.dp))
  }
}
