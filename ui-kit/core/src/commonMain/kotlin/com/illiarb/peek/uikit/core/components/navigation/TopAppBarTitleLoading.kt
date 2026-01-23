package com.illiarb.peek.uikit.core.components.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.atom.shimmer.ShimmerBox
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.valentinilk.shimmer.shimmer

@Composable
public fun TopAppBarTitleLoading() {
  ShimmerBox(
    Modifier
      .size(height = 16.dp, width = 80.dp)
      .shimmer()
  )
}

@Preview
@Composable
private fun TopAppBarTitleLoadingPreview() {
  PreviewTheme(darkMode = false) {
    TopAppBarTitleLoading()
  }
}

@Preview
@Composable
private fun TopAppBarTitleLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    TopAppBarTitleLoading()
  }
}
