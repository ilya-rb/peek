package com.illiarb.peek.features.home.articles

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.uikit.core.components.cell.ArticleCell
import com.illiarb.peek.uikit.core.components.cell.ArticleLoadingCell
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.model.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.bookmarks_stale_badge
import com.illiarb.peek.uikit.resources.home_articles_empty_action
import com.illiarb.peek.uikit.resources.home_articles_empty_title
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

internal sealed interface ArticlesUi {
  data class ArticleClicked(val item: Article) : ArticlesUi
  data class ArticleBookmarkClicked(val item: Article) : ArticlesUi
  data class ArticleSummarizeClicked(val item: Article) : ArticlesUi
  data class ArticleShareClicked(val item: Article) : ArticlesUi
  data object ArticlesRefreshClicked : ArticlesUi
}

@Composable
internal fun ArticlesLoading(modifier: Modifier = Modifier, contentPadding: PaddingValues) {
  LazyColumn(modifier = modifier, contentPadding = contentPadding) {
    items(
      count = 5,
      itemContent = {
        ArticleLoadingCell()

        HorizontalDivider(
          color = DividerDefaults.color.copy(alpha = 0.5f)
        )
      }
    )
  }
}

@Composable
internal fun ArticlesEmpty(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  eventSink: (ArticlesUi) -> Unit,
) {
  EmptyState(
    modifier = Modifier.padding(contentPadding),
    title = stringResource(Res.string.home_articles_empty_title),
    image = VectorIcon(
      imageVector = Icons.AutoMirrored.Filled.Article,
      contentDescription = "",
    ),
    buttonText = stringResource(Res.string.home_articles_empty_action),
    onButtonClick = { eventSink.invoke(ArticlesUi.ArticlesRefreshClicked) },
  )
}

@Composable
internal fun ArticlesContent(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  articles: ImmutableList<Article>,
  eventSink: (ArticlesUi) -> Unit,
  showStaleBadges: Boolean = false,
) {
  LazyColumn(modifier, contentPadding = contentPadding) {
    items(
      items = articles,
      key = { article -> article.url.url },
      itemContent = { article ->
        val badge = if (showStaleBadges && article.stale) {
          stringResource(Res.string.bookmarks_stale_badge)
        } else {
          null
        }
        ArticleCell(
          title = article.title,
          caption = article.dateFormatted,
          saved = article.saved,
          badge = badge,
          onClick = {
            eventSink.invoke(ArticlesUi.ArticleClicked(article))
          },
          onBookmarkClick = {
            eventSink.invoke(ArticlesUi.ArticleBookmarkClicked(article))
          },
          onSummarizeClick = {
            eventSink.invoke(ArticlesUi.ArticleSummarizeClicked(article))
          },
          onShareClick = {
            eventSink.invoke(ArticlesUi.ArticleShareClicked(article))
          },
        )
        HorizontalDivider(
          color = DividerDefaults.color.copy(alpha = 0.5f)
        )
      }
    )
  }
}

