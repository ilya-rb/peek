package com.illiarb.peek.features.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.atom.shimmer.ShimmerBox
import com.illiarb.peek.uikit.core.atom.shimmer.ShimmerColumn
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
internal fun ReaderLoading(modifier: Modifier = Modifier) {
  ShimmerColumn(modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
    ShimmerBox(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .height(300.dp)
    )
    ShimmerBox(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(bottom = 12.dp)
        .width(150.dp)
        .height(16.dp)
    )
    repeat(times = 3) { index ->
      ShimmerBox(
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .padding(bottom = 12.dp, top = if (index == 0) 16.dp else 0.dp)
          .fillMaxWidth()
          .height(16.dp)
      )
    }
    ShimmerBox(
      modifier = Modifier
        .padding(start = 16.dp, end = 50.dp, bottom = 12.dp, top = 16.dp)
        .width(150.dp)
        .height(16.dp)
    )
    ShimmerBox(
      modifier = Modifier
        .padding(start = 16.dp, end = 80.dp, bottom = 12.dp)
        .width(100.dp)
        .height(16.dp)
    )
  }
}

@Preview
@Composable
private fun ArticleReaderLoadingPreviewLight() {
  PreviewTheme(darkMode = false) {
    ReaderLoading(modifier = Modifier.fillMaxSize())
  }
}

@Preview
@Composable
private fun ArticleReaderLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    ReaderLoading(modifier = Modifier.fillMaxSize())
  }
}

