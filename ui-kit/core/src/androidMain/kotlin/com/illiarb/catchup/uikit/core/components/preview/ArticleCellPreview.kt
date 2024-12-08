package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.ArticleCell
import com.illiarb.catchup.uikit.core.components.ArticleLoadingCell
import com.illiarb.catchup.uikit.core.theme.UiKitTheme
import kotlin.random.Random

@Composable
fun ArticleCellPreview(darkTheme: Boolean) {
  UiKitTheme(useDynamicColors = false, useDarkTheme = darkTheme) {
    LazyColumn {
      items(
        count = 3,
        key = { index -> index },
        itemContent = {
          val length = Random.nextInt(1, 3)
          val title = "Title Title Title Title Title Title ".repeat(length)

          ArticleCell(
            title = title,
            caption = "Caption",
            author = "Author",
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
