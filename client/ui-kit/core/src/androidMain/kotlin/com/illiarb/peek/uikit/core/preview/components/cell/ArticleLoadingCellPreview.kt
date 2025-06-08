package com.illiarb.peek.uikit.core.preview.components.cell

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.components.cell.ArticleLoadingCell
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun ArticleCellLoadingPreviewLight() {
  PreviewTheme(darkMode = false) {
    LazyColumn {
      items(count = 3) {
        ArticleLoadingCell()
        HorizontalDivider()
      }
    }
  }
}

@Composable
@Preview
internal fun ArticleCellLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    LazyColumn {
      items(count = 3) {
        ArticleLoadingCell()
        HorizontalDivider()
      }
    }
  }
}