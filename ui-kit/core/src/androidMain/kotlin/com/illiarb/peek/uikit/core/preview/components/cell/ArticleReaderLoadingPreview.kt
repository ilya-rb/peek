package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.components.cell.ArticleReaderLoading
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun ArticleReaderLoadingLight() {
  PreviewTheme(darkMode = false, fullscreen = true) {
    ArticleReaderLoading()
  }
}

@Composable
@Preview
internal fun ArticleReaderLoadingDark() {
  PreviewTheme(darkMode = true, fullscreen = true) {
    ArticleReaderLoading()
  }
}
