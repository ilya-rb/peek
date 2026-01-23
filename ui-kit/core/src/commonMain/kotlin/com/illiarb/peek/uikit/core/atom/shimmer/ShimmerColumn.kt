package com.illiarb.peek.uikit.core.atom.shimmer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.valentinilk.shimmer.shimmer

@Composable
public fun ShimmerColumn(
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(modifier = modifier.shimmer(), content = content)
}

@Preview
@Composable
private fun ShimmerColumnPreviewLight() {
  PreviewTheme(darkMode = false) {
    ShimmerColumnPreview()
  }
}

@Preview
@Composable
private fun ShimmerColumnPreviewDark() {
  PreviewTheme(darkMode = true) {
    ShimmerColumnPreview()
  }
}

@Composable
private fun ShimmerColumnPreview() {
  ShimmerColumn(modifier = Modifier.padding(16.dp)) {
    ShimmerCircle(modifier = Modifier.width(48.dp).height(48.dp))
    Spacer(modifier = Modifier.height(16.dp))
    ShimmerBox(modifier = Modifier.width(200.dp).height(20.dp))
    Spacer(modifier = Modifier.height(8.dp))
    ShimmerBox(modifier = Modifier.width(150.dp).height(20.dp))
  }
}
