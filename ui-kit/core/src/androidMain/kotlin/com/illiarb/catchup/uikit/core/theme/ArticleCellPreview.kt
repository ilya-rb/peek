package com.illiarb.catchup.uikit.core.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.ArticleCell
import com.illiarb.catchup.uikit.core.components.ArticleLoadingCell

@Composable
fun ArticleCellPreview(darkTheme: Boolean) {
  UiKitTheme(useDynamicColors = false, useDarkTheme = darkTheme) {
    LazyColumn {
      items(
        count = 3,
        key = { index -> index },
        itemContent = {
          ArticleCell(
            title = "Title text Title text Title text Title text Title text Title text Title text",
            caption = "Technology",
          )
          HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
          )
        }
      )
    }
  }
}

@Composable
fun ArticleCellLoadingPreview(darkTheme: Boolean) {
  UiKitTheme(useDynamicColors = false, useDarkTheme = darkTheme) {
    ArticleLoadingCell()
  }
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFFF9F9FF,
)
fun ArticlePreviewLight() {
  ArticleCellPreview(darkTheme = false)
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFF111318,
)
fun ArticlePreviewDark() {
  ArticleCellPreview(darkTheme = true)
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFFF9F9FF,
)
fun ArticleCellLoadingPreviewLight() {
  ArticleCellLoadingPreview(darkTheme = false)
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFF111318,
)
fun ArticleCellLoadingPreviewDark() {
  ArticleCellLoadingPreview(darkTheme = true)
}
